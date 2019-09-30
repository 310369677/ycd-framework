package com.ycd.webflux.common.swagger;


import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.ycd.common.swagger.IgnoreSwaggerParameter;
import com.ycd.common.util.ReflectUtil;
import com.ycd.common.util.SimpleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.Types;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeField;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.isEmpty;
import static springfox.documentation.schema.Collections.collectionElementType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.isVoid;
import static springfox.documentation.schema.Types.typeNameFor;

public class CustomizeModelAttributeParameterExpander extends ModelAttributeParameterExpander {


    public CustomizeModelAttributeParameterExpander(FieldProvider fields, AccessorsProvider accessors, EnumTypeDeterminer enumTypeDeterminer) {
        super(fields, accessors, enumTypeDeterminer);
        this.fields = fields;
        this.accessors = accessors;
        this.enumTypeDeterminer = enumTypeDeterminer;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
    private final FieldProvider fields;
    private final AccessorsProvider accessors;
    private final EnumTypeDeterminer enumTypeDeterminer;

    @Autowired
    private DocumentationPluginsManager pluginsManager;

    @Override
    public List<Parameter> expand(ExpansionContext context) {
        List<Parameter> parameters = new ArrayList<>();
        Set<PropertyDescriptor> propertyDescriptors = propertyDescriptors(context.getParamType().getErasedType());
        List<Field> list = ReflectUtil.getAllFields(context.getParamType().getErasedType());
        //得到所有需要忽略的属性
        List<String> ignoreFieldName = ignoreFieldName(list);
        Map<Method, PropertyDescriptor> propertyLookupByGetter
                = propertyDescriptorsByMethod(context.getParamType().getErasedType(), propertyDescriptors);
        //移除对应的getter
        removeIgnoreFiledGetter(ignoreFieldName, propertyLookupByGetter);
        Iterable<ResolvedMethod> getters = accessors.in(context.getParamType()).stream()
                .filter(onlyValidGetters(propertyLookupByGetter.keySet())).collect(toList());

        Map<String, ResolvedField> fieldsByName =
                StreamSupport.stream(this.fields.in(context.getParamType()).spliterator(), false)
                        .collect(toMap((ResolvedMember::getName), identity()));
        //移除被忽略的属性
        removeIgnoreField(ignoreFieldName, fieldsByName);

        LOG.debug("Expanding parameter type: {}", context.getParamType());
        final AlternateTypeProvider alternateTypeProvider = context.getDocumentationContext().getAlternateTypeProvider();

        List<ModelAttributeField> attributes =
                allModelAttributes(
                        propertyLookupByGetter,
                        getters,
                        fieldsByName,
                        alternateTypeProvider);

        attributes.stream()
                .filter(simpleType().negate())
                .filter(recursiveType(context).negate())
                .forEach((each) -> {
                    LOG.debug("Attempting to expand expandable property: {}", each.getName());
                    parameters.addAll(
                            expand(
                                    context.childContext(
                                            nestedParentName(context.getParentName(), each),
                                            each.getFieldType(),
                                            context.getOperationContext())));
                });

        Stream<ModelAttributeField> collectionTypes = attributes.stream()
                .filter(isCollection().and(recursiveCollectionItemType(context.getParamType()).negate()));
        collectionTypes.forEachOrdered((each) -> {
            LOG.debug("Attempting to expand collection/array field: {}", each.getName());

            ResolvedType itemType = collectionElementType(each.getFieldType());
            if (Types.isBaseType(itemType) || enumTypeDeterminer.isEnum(itemType.getErasedType())) {
                parameters.add(simpleFields(context.getParentName(), context, each));
            } else {
                ExpansionContext childContext = context.childContext(
                        nestedParentName(context.getParentName(), each),
                        itemType,
                        context.getOperationContext());
                if (!context.hasSeenType(itemType)) {
                    parameters.addAll(expand(childContext));
                }
            }
        });

        Stream<ModelAttributeField> simpleFields = attributes.stream().filter(simpleType());
        simpleFields.forEach((each) -> {
            parameters.add(simpleFields(context.getParentName(), context, each));
        });
        return parameters.stream()
                .filter(((Predicate<Parameter>) Parameter::isHidden).negate())
                .filter(voidParameters().negate())
                .collect(toList());
    }

    private void removeIgnoreFiledGetter(List<String> ignoreFieldName, Map<Method, PropertyDescriptor> propertyLookupByGetter) {
        List<Method> needRemoveMethod = new ArrayList<>();
        for (Map.Entry<Method, PropertyDescriptor> entry : propertyLookupByGetter.entrySet()) {
            Method method = entry.getKey();
            String methodName = method.getName();
            String fieldName = "";
            if (methodName.startsWith("get")) {
                fieldName = methodName.substring(3);
                fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            } else if (methodName.startsWith("is")) {
                fieldName = methodName.substring(2);
                fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            }
            for (String ignoreName : ignoreFieldName) {
                if (fieldName.equals(ignoreName)) {
                    needRemoveMethod.add(method);
                }
            }
        }
        //移除方法
        for (Method removeMethod : needRemoveMethod) {
            propertyLookupByGetter.remove(removeMethod);
        }
    }

    private void removeIgnoreField(List<String> ignoreFieldName, Map<String, ResolvedField> fieldsByName) {
        if (SimpleUtil.isEmpty(ignoreFieldName) || SimpleUtil.isEmpty(fieldsByName)) {
            return;
        }
        for (String name : ignoreFieldName) {
            fieldsByName.remove(name);
        }
    }

    private List<String> ignoreFieldName(List<Field> list) {
        if (SimpleUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (Field field : list) {
            IgnoreSwaggerParameter t = field.getAnnotation(IgnoreSwaggerParameter.class);
            if (t != null) {
                result.add(field.getName());
            }
        }
        return result;
    }

    private Set<PropertyDescriptor> propertyDescriptors(final Class<?> clazz) {
        try {
            return new HashSet<>(Arrays.asList(getBeanInfo(clazz).getPropertyDescriptors()));
        } catch (IntrospectionException e) {
            LOG.warn(String.format("Failed to get bean properties on (%s)", clazz), e);
        }
        return emptySet();
    }

    BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz);
    }

    private Map<Method, PropertyDescriptor> propertyDescriptorsByMethod(
            final Class<?> clazz,
            Set<PropertyDescriptor> propertyDescriptors) {
        return propertyDescriptors.stream()
                .filter(input -> input.getReadMethod() != null
                        && !clazz.isAssignableFrom(Collection.class)
                        && !"isEmpty".equals(input.getReadMethod().getName()))
                .collect(toMap(PropertyDescriptor::getReadMethod, identity()));

    }

    private Predicate<ResolvedMethod> onlyValidGetters(final Set<Method> methods) {
        return input -> methods.contains(input.getRawMember());
    }

    private List<ModelAttributeField> allModelAttributes(
            Map<Method, PropertyDescriptor> propertyLookupByGetter,
            Iterable<ResolvedMethod> getters,
            Map<String, ResolvedField> fieldsByName,
            AlternateTypeProvider alternateTypeProvider) {

        Stream<ModelAttributeField> modelAttributesFromGetters = StreamSupport.stream(getters.spliterator(), false)
                .map(toModelAttributeField(fieldsByName, propertyLookupByGetter, alternateTypeProvider));

        Stream<ModelAttributeField> modelAttributesFromFields = fieldsByName.values().stream()
                .filter(ResolvedMember::isPublic)
                .map(toModelAttributeField(alternateTypeProvider));

        return Stream.concat(
                modelAttributesFromFields,
                modelAttributesFromGetters)
                .collect(toList());
    }

    private Predicate<ModelAttributeField> simpleType() {
        return isCollection().negate().and(isMap().negate())
                .and(
                        belongsToJavaPackage()
                                .or(isBaseType())
                                .or(isEnum()));
    }

    private Predicate<ModelAttributeField> isCollection() {
        return input -> isContainerType(input.getFieldType());
    }

    private Predicate<ModelAttributeField> isMap() {
        return input -> Maps.isMapType(input.getFieldType());
    }

    private Predicate<ModelAttributeField> belongsToJavaPackage() {
        return input -> ClassUtils.getPackageName(input.getFieldType().getErasedType()).startsWith("java.lang");
    }

    private Predicate<ModelAttributeField> isBaseType() {
        return input -> Types.isBaseType(input.getFieldType())
                || input.getFieldType().isPrimitive();
    }

    private Predicate<ModelAttributeField> isEnum() {
        return input -> enumTypeDeterminer.isEnum(input.getFieldType().getErasedType());
    }


    private Function<ResolvedMethod, ModelAttributeField> toModelAttributeField(
            final Map<String, ResolvedField> fieldsByName,
            final Map<Method, PropertyDescriptor> propertyLookupByGetter,
            final AlternateTypeProvider alternateTypeProvider) {
        return input -> {
            String name = propertyLookupByGetter.get(input.getRawMember()).getName();
            return new ModelAttributeField(
                    fieldType(alternateTypeProvider, input),
                    name,
                    input,
                    fieldsByName.get(name));
        };
    }

    private ResolvedType fieldType(AlternateTypeProvider alternateTypeProvider, ResolvedMethod method) {
        return alternateTypeProvider.alternateFor(method.getType());
    }

    private Function<ResolvedField, ModelAttributeField> toModelAttributeField(
            final AlternateTypeProvider alternateTypeProvider) {

        return input -> new ModelAttributeField(
                alternateTypeProvider.alternateFor(input.getType()),
                input.getName(),
                input,
                input);
    }

    private Predicate<ModelAttributeField> recursiveType(final ExpansionContext context) {
        return input -> context.hasSeenType(input.getFieldType());
    }

    private String nestedParentName(String parentName, ModelAttributeField attribute) {
        String name = attribute.getName();
        ResolvedType fieldType = attribute.getFieldType();
        if (isContainerType(fieldType) && !Types.isBaseType(collectionElementType(fieldType))) {
            name += "[0]";
        }

        if (isEmpty(parentName)) {
            return name;
        }
        return String.format("%s.%s", parentName, name);
    }

    private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
        return input -> Objects.equals(collectionElementType(input.getFieldType()), paramType);
    }

    private Parameter simpleFields(
            String parentName,
            ExpansionContext context,
            ModelAttributeField each) {
        LOG.debug("Attempting to expand field: {}", each);
        String dataTypeName = ofNullable(typeNameFor(each.getFieldType().getErasedType()))
                .orElse(each.getFieldType().getErasedType().getSimpleName());
        LOG.debug("Building parameter for field: {}, with type: ", each, each.getFieldType());
        ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
                dataTypeName,
                parentName,
                ParameterTypeDeterminer.determineScalarParameterType(
                        context.getOperationContext().consumes(),
                        context.getOperationContext().httpMethod()),
                new ModelAttributeParameterMetadataAccessor(
                        each.annotatedElements(),
                        each.getFieldType(),
                        each.getName()),
                context.getDocumentationContext().getDocumentationType(),
                new ParameterBuilder());
        return pluginsManager.expandParameter(parameterExpansionContext);
    }


    private Predicate<Parameter> voidParameters() {
        return input -> isVoid(input.getType().orElse(null));
    }
}

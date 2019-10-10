package com.ycd.webflux.common.config;



import com.ycd.common.config.CommonConfig;
import com.ycd.webflux.common.swagger.CustomizeModelAttributeParameterExpander;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Configuration
@EnableSwagger2WebFlux
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(CommonConfig.class)
public class Swagger2 {


    @Bean
    public Docket restApi(CommonConfig commonConfig) {
        //=====添加head参数start============================
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name(commonConfig.getTokenAuthHead()).description("AccessToken令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        // =========添加head参数end===================

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(commonConfig.isSwaggerEnable())
                .select()
                .apis(basePackage(commonConfig.getSwaggerScanBasePackage()))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars)
                .apiInfo(apiInfo(commonConfig));
    }

    private ApiInfo apiInfo(CommonConfig commonConfig) {
        return new ApiInfoBuilder().title(commonConfig.getSwaggerApiDocumentTitle())
                .description(commonConfig.getSwaggerApiDocumentDesc())
                .version(commonConfig.getSwaggerApiVersion())
                .build();

    }

    @Bean
    @Primary
    public ModelAttributeParameterExpander modelAttributeParameterExpander(FieldProvider fields, AccessorsProvider accessors, EnumTypeDeterminer enumTypeDeterminer) {
        return new CustomizeModelAttributeParameterExpander(fields, accessors, enumTypeDeterminer);
    }

    private static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).map(handlerPackage(basePackage)).orElse(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(",")) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.ofNullable(input.declaringClass());
    }

}

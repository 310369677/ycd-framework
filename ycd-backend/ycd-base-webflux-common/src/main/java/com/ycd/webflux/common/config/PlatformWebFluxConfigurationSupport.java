package com.ycd.webflux.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ycd.common.util.SimpleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


@Configuration
@EnableConfigurationProperties({ResourceProperties.class, WebFluxProperties.class, ServerProperties.class, WebFluxAutoConfiguration.EnableWebFluxConfiguration.class, JacksonProperties.class})
@AutoConfigureAfter({
        CodecsAutoConfiguration.class, ValidationAutoConfiguration.class})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 11)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class PlatformWebFluxConfigurationSupport extends WebFluxAutoConfiguration.WebFluxConfig {


    private static final Logger log = LoggerFactory.getLogger(PlatformWebFluxConfigurationSupport.class);

    @Autowired
    JacksonProperties jacksonProperties;

    public PlatformWebFluxConfigurationSupport(ResourceProperties resourceProperties, WebFluxProperties webFluxProperties, ListableBeanFactory beanFactory, ObjectProvider<HandlerMethodArgumentResolver> resolvers, ObjectProvider<CodecCustomizer> codecCustomizers, ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizer, ObjectProvider<ViewResolver> viewResolvers) {
        super(resourceProperties, webFluxProperties, beanFactory, resolvers, codecCustomizers, resourceHandlerRegistrationCustomizer, viewResolvers);
    }


    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        super.configureArgumentResolvers(configurer);
        configurer.addCustomResolver(requestParamResolverOverride());
    }


    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.customCodecs().encoder(long2StringEncoder());
        super.configureHttpMessageCodecs(configurer);

    }


    @Autowired
    ConfigurableApplicationContext applicationContext;


    private Encoder long2StringEncoder() {
        return new Jackson2JsonEncoder() {


            private ObjectWriter objectWriter;

            DefaultSerializerProvider provider = null;

            @Override
            public ObjectMapper getObjectMapper() {
                ObjectMapper mapper = super.getObjectMapper();
                configMapper(mapper);
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
                simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
                mapper.registerModule(simpleModule);
                if (objectWriter == null) {
                    objectWriter = new ObjectWriter(mapper, mapper.getSerializationConfig()) {
                        private static final long serialVersionUID = 2058291700866798227L;

                        @Override
                        public void writeValue(JsonGenerator gen, Object value) throws IOException {
                            if (provider == null) {
                                SerializationConfig config = mapper.getSerializationConfig();
                                config.initialize(gen);
                                provider = new DefaultSerializerProvider.Impl().createInstance(config, mapper.getSerializerFactory());
                            }
                            provider.serializeValue(gen, value);
                        }
                    };
                }
                return mapper;
            }

            private void configMapper(ObjectMapper mapper) {
                if (SimpleUtil.isNotEmpty(jacksonProperties.getDefaultPropertyInclusion())) {
                    mapper.setSerializationInclusion(jacksonProperties.getDefaultPropertyInclusion());
                }
                if (SimpleUtil.isNotEmpty(jacksonProperties.getDateFormat())) {
                    mapper.setDateFormat(new SimpleDateFormat(jacksonProperties.getDateFormat()));
                }
                if (SimpleUtil.isNotEmpty(jacksonProperties.getLocale())) {
                    mapper.setLocale(jacksonProperties.getLocale());
                }
            }

            @Override
            protected ObjectWriter customizeWriter(ObjectWriter writer, MimeType mimeType, ResolvableType elementType, Map<String, Object> hints) {
                return objectWriter;
            }
        };
    }


    /**
     * 参数解析器，解决post方式传递时单个参数无法解析
     *
     * @return 转换器
     */
    private HandlerMethodArgumentResolver requestParamResolverOverride() {
        return new RequestParamMethodArgumentResolver(applicationContext.getBeanFactory(), new ReactiveAdapterRegistry(), true) {
            @Override
            protected Object resolveNamedValue(String name, MethodParameter parameter, ServerWebExchange exchange) {
                List<String> paramValues = exchange.getRequest().getQueryParams().get(name);
                List<String> formVal = null;
                MultiValueMap<String, String> map = exchange.getFormData().block();
                if (SimpleUtil.isNotEmpty(map)) {
                    formVal = map.get(name);
                }
                Object result = null;
                if (paramValues != null) {
                    result = (paramValues.size() == 1 ? paramValues.get(0) : paramValues);
                }
                if ((result == null) && SimpleUtil.isNotEmpty(formVal)) {
                    result = (formVal.size() == 1 ? formVal.get(0) : formVal);
                }
                return result;
            }
        };
    }


}



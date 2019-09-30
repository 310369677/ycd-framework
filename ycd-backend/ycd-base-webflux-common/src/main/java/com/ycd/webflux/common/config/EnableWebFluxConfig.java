package com.ycd.webflux.common.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler;

import java.util.Collections;
import java.util.List;


@Configuration
@Primary
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(WebFluxProperties.class)
public class EnableWebFluxConfig extends WebFluxAutoConfiguration.EnableWebFluxConfiguration {


    public EnableWebFluxConfig(WebFluxProperties webFluxProperties, ObjectProvider<WebFluxRegistrations> webFluxRegistrations) {
        super(webFluxProperties, webFluxRegistrations);
    }


    @Override
    @Bean
    public ResponseEntityResultHandler responseEntityResultHandler() {
        List<HttpMessageWriter<?>> list = serverCodecConfigurer().getWriters();
        Collections.reverse(list);
        return new ResponseEntityResultHandler(list,
                webFluxContentTypeResolver(), webFluxAdapterRegistry());
    }


    @Bean
    public ResponseBodyResultHandler responseBodyResultHandler() {
        List<HttpMessageWriter<?>> list = serverCodecConfigurer().getWriters();
        Collections.reverse(list);
        return new ResponseBodyResultHandler(list,
                webFluxContentTypeResolver(), webFluxAdapterRegistry());
    }
}

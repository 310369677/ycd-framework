package com.ycd.servlet.common.swagger;


import com.ycd.common.config.CommonConfig;
import com.ycd.servlet.common.swagger.CustomizeModelAttributeParameterExpander;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-07-05
 */
@Configuration
@EnableSwagger2WebMvc
@EnableConfigurationProperties(CommonConfig.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletSwagger2 {

    @Bean
    public Docket restApi(CommonConfig commonConfig) {
        //=====添加head参数start============================
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name(commonConfig.getAuthHeader()).description("AccessToken令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        // =========添加head参数end===================

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(commonConfig.isSwaggerEnable())
                .select()
                .apis(RequestHandlerSelectors.basePackage(commonConfig.getBasePackage()))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars)
                .apiInfo(apiInfo(commonConfig));
    }

    private ApiInfo apiInfo(CommonConfig commonConfig) {
        return new ApiInfoBuilder().title(commonConfig.getApiDocumentTitle())
                .description(commonConfig.getApiDocumentDesc())
                //.termsOfServiceUrl("https://blog.csdn.net/weixin_37591536")
                .version(commonConfig.getVersion())
                .build();

    }

    @Bean
    @Primary
    @ConditionalOnClass(FieldProvider.class)
    public ModelAttributeParameterExpander modelAttributeParameterExpander(FieldProvider fields, AccessorsProvider accessors, EnumTypeDeterminer enumTypeDeterminer) {
        return new CustomizeModelAttributeParameterExpander(fields, accessors, enumTypeDeterminer);
    }
}

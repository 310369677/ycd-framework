package com.ycd.servlet.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ycd.common.config.CommonConfig;
import com.ycd.servlet.common.cache.RedisUserCache;
import com.ycd.servlet.common.cache.UserCacheProxy;
import com.ycd.servlet.common.cache.UserMultiLoginCache;
import com.ycd.servlet.common.cache.UserSingleLoginCache;
import com.ycd.servlet.common.filter.PageFilter;
import com.ycd.servlet.common.filter.UserInfoFilter;
import com.ycd.servlet.common.service.impl.DicServiceImpl;
import com.ycd.servlet.common.service.impl.DocumentMappingServiceImpl;
import com.ycd.servlet.common.service.impl.DocumentServiceImpl;
import com.ycd.servlet.common.service.interfaces.DicService;
import com.ycd.servlet.common.service.interfaces.DocumentMappingService;
import com.ycd.servlet.common.service.interfaces.DocumentService;
import com.ycd.servlet.common.web.DicController;
import com.ycd.servlet.common.web.DocumentController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.Filter;


@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(CommonConfig.class)
public class MvcBeanConfig {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 100)
    public Filter pageFilter() {
        return new PageFilter();
    }


    /**
     * 设置用户过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(prefix = "base.common",name = "userfilter",havingValue = "true")
    public UserInfoFilter userInfoFilter(CommonConfig commonConfig, RedisUserCache redisUserCache) {
        UserInfoFilter userInfoFilter = new UserInfoFilter();
        userInfoFilter.setConfig(commonConfig);
        userInfoFilter.setRedisUserCache(redisUserCache);
        return userInfoFilter;
    }

    @Bean
    public DocumentController documentController() {
        return new DocumentController();
    }


    @Bean
    public DocumentService documentService() {
        return new DocumentServiceImpl();
    }

    @Bean
    public DocumentMappingService documentMappingService() {
        return new DocumentMappingServiceImpl();
    }


    @Bean
    public DicController dicController() {
        return new DicController();
    }

    @Bean
    public DicService dicService() {
        return new DicServiceImpl();
    }

    @Bean
    public RedisUserCache redisUserCache() {
        return new UserCacheProxy();
    }

    @Bean
    public UserSingleLoginCache userSingleLoginCache() {
        return new UserSingleLoginCache();
    }

    @Bean
    public UserMultiLoginCache userMultiLoginCache() {
        return new UserMultiLoginCache();
    }

    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        //不显示为null的字段
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);
        return new MappingJackson2HttpMessageConverter(mapper);
    }


}

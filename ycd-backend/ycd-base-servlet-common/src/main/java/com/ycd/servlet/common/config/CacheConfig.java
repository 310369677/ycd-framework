package com.ycd.servlet.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CacheConfig {
}

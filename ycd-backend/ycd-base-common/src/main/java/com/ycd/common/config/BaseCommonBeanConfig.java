package com.ycd.common.config;

import com.ycd.common.mybatis.interceptor.ExecutorInterceptor;
import com.ycd.common.mybatis.interceptor.SqlStatementInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseCommonBeanConfig {

    @Bean
    public ExecutorInterceptor executorInterceptor() {
        return new ExecutorInterceptor();
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }
}

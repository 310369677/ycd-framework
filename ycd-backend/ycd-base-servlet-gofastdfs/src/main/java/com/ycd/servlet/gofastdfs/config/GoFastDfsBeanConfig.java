package com.ycd.servlet.gofastdfs.config;

import com.ycd.servlet.gofastdfs.controller.GoFastDocumentController;
import com.ycd.servlet.gofastdfs.service.impl.GoFastDfsDocumentServiceImpl;
import com.ycd.servlet.gofastdfs.service.impl.interfaces.GoFastDfsDocumentService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoFastDfsConfigProperties.class)
public class GoFastDfsBeanConfig {

    @Bean
    public GoFastDocumentController goFastDocumentController() {
        return new GoFastDocumentController();
    }

    @Bean
    public GoFastDfsDocumentService goFastDfsDocumentService() {
        return new GoFastDfsDocumentServiceImpl();
    }
}

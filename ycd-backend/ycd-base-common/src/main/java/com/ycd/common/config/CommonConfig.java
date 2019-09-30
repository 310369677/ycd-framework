package com.ycd.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "base.common")
public class CommonConfig {


    private String fileStorePath = "~/document";

    private String authHeader = "auth";

    private String basePackage = "com.ycd";

    private boolean swaggerEnable = false;

    private String apiDocumentTitle = "api文档";

    private String apiDocumentDesc = "应用接口文档";

    private String version = "unknowned";


    private String userLoginModel = "2";

    private Long userLoginExpireTime = 1800L;

    private String tokenAuthHead = "auth";


    public String getFileStorePath() {
        return fileStorePath;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = fileStorePath;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public boolean isSwaggerEnable() {
        return swaggerEnable;
    }

    public void setSwaggerEnable(boolean swaggerEnable) {
        this.swaggerEnable = swaggerEnable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiDocumentTitle() {
        return apiDocumentTitle;
    }

    public void setApiDocumentTitle(String apiDocumentTitle) {
        this.apiDocumentTitle = apiDocumentTitle;
    }

    public String getApiDocumentDesc() {
        return apiDocumentDesc;
    }

    public void setApiDocumentDesc(String apiDocumentDesc) {
        this.apiDocumentDesc = apiDocumentDesc;
    }


    public String getUserLoginModel() {
        return userLoginModel;
    }

    public void setUserLoginModel(String userLoginModel) {
        this.userLoginModel = userLoginModel;
    }

    public Long getUserLoginExpireTime() {
        return userLoginExpireTime;
    }

    public void setUserLoginExpireTime(Long userLoginExpireTime) {
        this.userLoginExpireTime = userLoginExpireTime;
    }

    public String getTokenAuthHead() {
        return tokenAuthHead;
    }

    public void setTokenAuthHead(String tokenAuthHead) {
        this.tokenAuthHead = tokenAuthHead;
    }
}

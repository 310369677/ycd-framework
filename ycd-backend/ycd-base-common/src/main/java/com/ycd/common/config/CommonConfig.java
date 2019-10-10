package com.ycd.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "base.common")
public class CommonConfig {


    private String fileStorePath = "~/document";


    private String swaggerScanBasePackage = "com.ycd";

    private boolean swaggerEnable = false;

    private String swaggerApiDocumentTitle = "api文档";

    private String swaggerApiDocumentDesc = "应用接口文档";

    private String swaggerApiVersion = "unknowned";


    private String userLoginModel = "2";

    private Long userLoginExpireTime = 1800L;

    private String tokenAuthHead = "auth";


    public String getFileStorePath() {
        return fileStorePath;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = fileStorePath;
    }


    public boolean isSwaggerEnable() {
        return swaggerEnable;
    }

    public void setSwaggerEnable(boolean swaggerEnable) {
        this.swaggerEnable = swaggerEnable;
    }


    public String getSwaggerApiDocumentTitle() {
        return swaggerApiDocumentTitle;
    }

    public void setSwaggerApiDocumentTitle(String swaggerApiDocumentTitle) {
        this.swaggerApiDocumentTitle = swaggerApiDocumentTitle;
    }

    public String getSwaggerApiDocumentDesc() {
        return swaggerApiDocumentDesc;
    }

    public void setSwaggerApiDocumentDesc(String swaggerApiDocumentDesc) {
        this.swaggerApiDocumentDesc = swaggerApiDocumentDesc;
    }

    public String getSwaggerApiVersion() {
        return swaggerApiVersion;
    }

    public void setSwaggerApiVersion(String swaggerApiVersion) {
        this.swaggerApiVersion = swaggerApiVersion;
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


    public String getSwaggerScanBasePackage() {
        return swaggerScanBasePackage;
    }

    public void setSwaggerScanBasePackage(String swaggerScanBasePackage) {
        this.swaggerScanBasePackage = swaggerScanBasePackage;
    }
}

package com.ycd.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "base.common")
public class CommonConfig {


    /**
     * 本机存储时，文档的存储路劲
     */

    private String fileStorePath = "~/document";


    /**
     * swagger扫描的基础包，可以用逗号分隔
     */
    private String swaggerScanBasePackage = "com.ycd";

    /**
     * swagger文档是否可见
     */
    private boolean swaggerEnable = false;

    /**
     * swaggerApi文档标题
     */
    private String swaggerApiDocumentTitle = "api文档";

    /**
     * swaggerApi文档的描述
     */
    private String swaggerApiDocumentDesc = "应用接口文档";

    /**
     * swaggerApi的版本
     */
    private String swaggerApiVersion = "unknowned";

    /**
     * 用户登录模型
     */
    private String userLoginModel = "2";

    /**
     * 用户登录过期时间，单位是s
     */
    private Long userLoginExpireTime = 1800L;

    /**
     * 授权头中，token的key值
     */
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

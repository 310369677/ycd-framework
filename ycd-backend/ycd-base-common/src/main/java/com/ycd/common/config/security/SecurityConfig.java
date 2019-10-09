package com.ycd.common.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "base.security")
public class SecurityConfig {


    private String annoUrls = "/**";

    private String loginUrl = "/login";

    private String logOutUrl = "/logout";

    /**
     * 是否打开验证码,默认不打开
     */
    private boolean openCode = false;


    public String getAnnoUrls() {
        return annoUrls;
    }

    public void setAnnoUrls(String annoUrls) {
        this.annoUrls = annoUrls;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogOutUrl() {
        return logOutUrl;
    }

    public void setLogOutUrl(String logOutUrl) {
        this.logOutUrl = logOutUrl;
    }

    public boolean isOpenCode() {
        return openCode;
    }

    public void setOpenCode(boolean openCode) {
        this.openCode = openCode;
    }
}
package com.ycd.common.constant;

public enum StatusCode {

    NO_AUTHTICATION("401", "未认证的请求"),
    NO_PRIVILEGE("403", "权限不足"),
    NO_FOUND("404", "资源未找到"),
    OK("200", "成功"),
    ERROR("500", "失败"),
    TOKEN_INVALID("2000", "token失效");


    StatusCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }


}

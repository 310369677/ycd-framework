package com.ycd.common.constant;

/**
 * 描述:boolean常量
 * 作者:杨川东
 * 日期:2019-06-01
 */
public enum BooleanEnum {
    NO("0", "否"), YES("1", "是");

    BooleanEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public String code() {
        return this.code;
    }

    public String desc() {
        return desc;
    }
}

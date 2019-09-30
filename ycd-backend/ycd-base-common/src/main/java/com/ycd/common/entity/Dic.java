package com.ycd.common.entity;


import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Table;

/**
 * 字典表
 */
@Table(name = "t_dic")
public class Dic extends AbstractEntity {

    /**
     * 字典的名字
     */
    @ApiModelProperty(name = "name", value = "字典名字")
    private String name;

    /**
     * 字典的key
     */
    @ApiModelProperty(name = "dicKey", value = "字典的key")
    private String dicKey;

    /**
     * 字典的value
     */
    @ApiModelProperty(name = "dicValue", value = "字典的value")
    private String dicValue;

    /**
     * 字典的类型:1普通，2树
     */
    @ApiModelProperty(name = "dicType", value = "字典的类型:1普通，2树")
    private String dicType;

    /**
     * 当字典的类型为2的时候，这个字段有用
     */
    @ApiModelProperty(name = "parentId", value = "当字典的类型是树的时候,可以指定父级的id")
    private String parentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


    public String getDicKey() {
        return dicKey;
    }

    public void setDicKey(String dicKey) {
        this.dicKey = dicKey;
    }

    public String getDicValue() {
        return dicValue;
    }

    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }

    public String getDicType() {
        return dicType;
    }

    public void setDicType(String dicType) {
        this.dicType = dicType;
    }
}

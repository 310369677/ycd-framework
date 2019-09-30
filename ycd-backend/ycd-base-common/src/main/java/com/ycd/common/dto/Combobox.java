package com.ycd.common.dto;

import java.io.Serializable;

public class Combobox implements Serializable {
    private static final long serialVersionUID = -8987111540643747413L;

    private String id;

    private String text;

    private String parentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

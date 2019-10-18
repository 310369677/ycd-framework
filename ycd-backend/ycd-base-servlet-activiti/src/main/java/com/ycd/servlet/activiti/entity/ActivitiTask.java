package com.ycd.servlet.activiti.entity;

import com.ycd.common.util.SimpleUtil;

import java.util.Date;

public class ActivitiTask {

    private String id;

    private String assignee;

    private String name;

    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = SimpleUtil.formatDate(createTime, "yyyy-MM-dd HH:mm:ss");
    }


}

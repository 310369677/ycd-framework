package com.ycd.common.entity.security;


import com.ycd.common.entity.AbstractEntity;

import javax.persistence.Table;

@Table(name = "t_user_role")
public class UserWithRole extends AbstractEntity {

    private String userId;

    private String roleId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}

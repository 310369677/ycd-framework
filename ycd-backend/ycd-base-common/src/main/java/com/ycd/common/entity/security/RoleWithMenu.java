package com.ycd.common.entity.security;


import com.ycd.common.entity.AbstractEntity;

import javax.persistence.Table;

/**
 * 角色菜单
 */
@Table(name = "t_role_menu")
public class RoleWithMenu extends AbstractEntity {

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 菜单id
     */
    private String menuId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}

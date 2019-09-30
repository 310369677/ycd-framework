package com.ycd.servlet.security.service.interfaces;


import com.ycd.common.entity.security.RoleWithMenu;
import com.ycd.servlet.common.service.interfaces.LongPriService;

public interface RoleWithMenuService<T extends RoleWithMenu> extends LongPriService<T> {
    void relationRoleAndMenu(String roleId, String menuId);

    void relationRoleAndMenus(String roleId, String menuIds);
}

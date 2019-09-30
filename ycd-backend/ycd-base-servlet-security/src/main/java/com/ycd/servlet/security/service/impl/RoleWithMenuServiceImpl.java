package com.ycd.servlet.security.service.impl;


import com.ycd.common.entity.security.RoleWithMenu;
import com.ycd.common.repo.mybatis.security.DefaultMenuMapper;
import com.ycd.common.repo.mybatis.security.DefaultRoleMapper;
import com.ycd.common.repo.mybatis.security.RoleMenuMapper;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.impl.AbstractServiceWithCreateEntity;
import com.ycd.servlet.security.service.interfaces.RoleWithMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public class RoleWithMenuServiceImpl<T extends RoleWithMenu> extends AbstractServiceWithCreateEntity<T> implements RoleWithMenuService<T> {

    @Autowired
    RoleMenuMapper roleMenuMapper;

    @Autowired
    DefaultRoleMapper roleMapper;

    @Autowired
    DefaultMenuMapper menuMapper;

    @Override
    @Transactional
    public void relationRoleAndMenu(String roleId, String menuId) {
        if (SimpleUtil.isEmpty(roleId) || SimpleUtil.isEmpty(menuId)) {
            return;
        }
        RoleWithMenu roleWithMenu = new RoleWithMenu();
        roleWithMenu.setRoleId(roleId);
        roleWithMenu.setMenuId(menuId);
        RoleWithMenu dataBase = roleMenuMapper.selectOne(roleWithMenu);
        if (SimpleUtil.isNotEmpty(dataBase)) {
            return;
        }
        //判断角色和菜单必须存在
        SimpleUtil.assertNotEmpty(roleMapper.selectByPrimaryKey(Long.valueOf(roleId)), "无效的角色id");
        SimpleUtil.assertNotEmpty(menuMapper.selectByPrimaryKey(Long.valueOf(menuId)), "无效的菜单id");
        int count = roleMenuMapper.insertSelective(roleWithMenu);
        LOGGER.debug("角色id:{},菜单id:{}关联成功", roleId, menuId);
        SimpleUtil.trueAndThrows(count != 1, "关联角色和菜单失败");
    }

    @Override
    @Transactional
    public void relationRoleAndMenus(String roleId, String menuIds) {
        if (SimpleUtil.isEmpty(roleId) || SimpleUtil.isEmpty(menuIds)) {
            return;
        }
        Stream.of(menuIds.split(","))
                .forEach(menuId -> {
                    relationRoleAndMenu(roleId, menuId);
                });
    }
}

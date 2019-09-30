package com.ycd.servlet.security.service.impl;


import com.ycd.common.entity.security.UserWithRole;
import com.ycd.common.repo.mybatis.security.DefaultRoleMapper;
import com.ycd.common.repo.mybatis.security.DefaultUserMapper;
import com.ycd.common.repo.mybatis.security.UserRoleMapper;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.impl.AbstractServiceWithCreateEntity;
import com.ycd.servlet.security.service.interfaces.UserWithRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public class UserWithRoleServiceImpl<T extends UserWithRole> extends AbstractServiceWithCreateEntity<T> implements UserWithRoleService<T> {
    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    DefaultUserMapper userMapper;

    @Autowired
    DefaultRoleMapper roleMapper;

    @Override
    @Transactional
    public void relationUserAndRole(String userId, String roleId) {
        if (SimpleUtil.isEmpty(userId) || SimpleUtil.isEmpty(roleId)) {
            return;
        }
        UserWithRole userWithRole = new UserWithRole();
        userWithRole.setRoleId(roleId);
        userWithRole.setUserId(userId);
        UserWithRole dataBase = userRoleMapper.selectOne(userWithRole);
        if (SimpleUtil.isNotEmpty(dataBase)) {
            return;
        }
        SimpleUtil.assertNotEmpty(userMapper.selectByPrimaryKey(Long.valueOf(userId)), "无效的用户id");
        SimpleUtil.assertNotEmpty(roleMapper.selectByPrimaryKey(Long.valueOf(roleId)), "无效的角色id");
        int count = userRoleMapper.insertSelective(userWithRole);
        SimpleUtil.trueAndThrows(count != 1, "关联用户和角色失败");
    }

    @Override
    @Transactional
    public void relationUserAndRoles(String userId, String roleIds) {
        if (SimpleUtil.isEmpty(userId) || SimpleUtil.isEmpty(roleIds)) {
            return;
        }
        Stream.of(roleIds.split(",")).forEach(t -> {
            relationUserAndRole(userId, t);
        });
    }
}

package com.ycd.servlet.security.service.impl;


import com.ycd.common.entity.security.Role;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.impl.AbstractServiceWithCreateEntity;
import com.ycd.servlet.security.service.interfaces.RoleService;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public class AbstractRoleServiceImpl<T extends Role> extends AbstractServiceWithCreateEntity<T> implements RoleService<T> {
    @Override
    @Transactional
    public Long saveRole(T t) {
        SimpleUtil.assertNotEmpty(t.getName(), "角色名字不能为空");
        return save(t);
    }

    @Override
    @Transactional
    public void updateRole(T t) {
        SimpleUtil.assertNotEmpty(t.getId(), "id不能为空");
        SimpleUtil.assertNotEmpty(t.getName(), "角色名字不能为空");
        update(t);
    }

    @Override
    @Transactional
    public void deleteRolesByIds(String ids) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        Stream.of(ids.split(",")).map(Long::valueOf)
                .forEach(this::deleteById);
    }
}

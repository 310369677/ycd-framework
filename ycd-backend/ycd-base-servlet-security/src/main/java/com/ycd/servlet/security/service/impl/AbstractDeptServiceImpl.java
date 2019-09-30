package com.ycd.servlet.security.service.impl;


import com.ycd.common.entity.security.Dept;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.impl.AbstractServiceWithCreateEntity;
import com.ycd.servlet.security.service.interfaces.DeptService;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public class AbstractDeptServiceImpl<T extends Dept> extends AbstractServiceWithCreateEntity<T> implements DeptService<T> {
    @Override
    @Transactional
    public Long saveDept(T t) {
        SimpleUtil.assertNotEmpty(t.getDeptName(), "部门名字不能为空");
        //校验父级部门必须存在
        if (SimpleUtil.isNotEmpty(t.getParentId())) {
            SimpleUtil.assertNotEmpty(mapper.selectByPrimaryKey(t.getParentId()), "无效的父级id");
        }
        return save(t);
    }

    @Override
    @Transactional
    public void updateDept(T t) {
        SimpleUtil.assertNotEmpty(t.getId(), "部门的id不能为空");
        t.setParentId(null);
        update(t);
    }

    @Override
    @Transactional
    public void deleteDeptByIds(String ids) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        Stream.of(ids.split(",")).map(Long::valueOf)
                .forEach(this::deleteById);
    }
}

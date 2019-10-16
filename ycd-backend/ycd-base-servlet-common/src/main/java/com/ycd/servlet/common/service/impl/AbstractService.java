package com.ycd.servlet.common.service.impl;


import com.ycd.common.entity.Entity;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.interfaces.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractService<ID, T extends Entity<ID>> implements Service<ID, T> {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    protected Mapper<T> mapper;


    @Override
    @Transactional
    public ID save(T t) {
        SimpleUtil.assertNotEmpty(t, "对象不能为空");
        int count = mapper.insertSelective(t);
        return count > 0 ? t.id() : null;

    }

    @Override
    @Transactional
    public List<ID> saveList(List<T> list) {
        if (SimpleUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<ID> result = new ArrayList<>();
        for (T t : list) {
            ID id = save(t);
            if (SimpleUtil.isNotEmpty(id)) {
                result.add(id);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public T update(T t) {
        if (SimpleUtil.isEmpty(t) || SimpleUtil.isEmpty(t.id())) {
            return null;
        }
        int result = mapper.updateByPrimaryKeySelective(t);
        return result > 0 ? t : null;
    }

    @Override
    @Transactional
    public List<T> updateList(List<T> list) {
        if (SimpleUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        for (T t : list) {
            T t1 = update(t);
            if (SimpleUtil.isNotEmpty(t1)) {
                result.add(t1);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        if (SimpleUtil.isEmpty(id)) {
            return;
        }
        mapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void deleteByIds(List<ID> ids) {
        for (ID id : ids) {
            deleteById(id);
        }
    }

    @Override
    @Transactional
    public T findById(ID id) {
        if (SimpleUtil.isEmpty(id)) {
            return null;
        }
        return mapper.selectByPrimaryKey(id);
    }
}

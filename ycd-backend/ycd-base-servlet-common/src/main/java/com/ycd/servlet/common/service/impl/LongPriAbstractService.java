package com.ycd.servlet.common.service.impl;



import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.util.SimpleUtil;
import tk.mybatis.mapper.entity.Example;


public abstract class LongPriAbstractService<T extends AbstractEntity> extends AbstractService<Long, T> {


    @Override
    public T update(T t) {
        if (SimpleUtil.isEmpty(t) || SimpleUtil.isEmpty(t.id())) {
            return null;
        }
        //查询出原对象
        T data = mapper.selectByPrimaryKey(t.id());
        //创建example
        Example example = new Example(t.getClass());
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("version", data.getVersion());
        t.setVersion(data.getVersion() + 1);
        int result = mapper.updateByExampleSelective(t, example);
        return result > 0 ? t : null;
    }
}

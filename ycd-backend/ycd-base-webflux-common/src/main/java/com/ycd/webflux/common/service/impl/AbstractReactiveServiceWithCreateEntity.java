package com.ycd.webflux.common.service.impl;


import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.util.SimpleUtil;
import reactor.core.publisher.Mono;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractReactiveServiceWithCreateEntity<T extends AbstractEntity> extends LongPriAbstractReactiveService<T> {

    private Class<T> clazz = null;

    public AbstractReactiveServiceWithCreateEntity() {
        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType pType = (ParameterizedType) type;
        this.clazz = (Class<T>) pType.getActualTypeArguments()[0];
    }


    protected T newEntityInstance() {
        SimpleUtil.assertNotEmpty(clazz, "clazz not empty");
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<T> update(T t) {
        return Mono.fromSupplier(() -> {
            if (SimpleUtil.isEmpty(t) || SimpleUtil.isEmpty(t.id())) {
                return null;
            }
            //查询出原对象
            T data = mapper.selectByPrimaryKey(t.id());
            //创建example
            Example example = new Example(clazz);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("version", data.getVersion());
            t.setVersion(data.getVersion() + 1);
            criteria.andEqualTo("id", data.getId());
            int result = mapper.updateByExampleSelective(t, example);
            SimpleUtil.trueAndThrows(result != 1, "修改失败,该数据可能已经被其他人修改,请刷新重试");
            return t;
        });

    }
}

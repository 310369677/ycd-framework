package com.ycd.webflux.common.service.impl;



import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import reactor.core.publisher.Mono;
import tk.mybatis.mapper.entity.Example;


public abstract class LongPriAbstractReactiveService<T extends AbstractEntity> extends AbstractReactiveService<Long, T> {


    @Override
    @WebFluxTransactional
    public Mono<T> update(T t) {
        return Mono.fromSupplier(() -> {
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
            SimpleUtil.trueAndThrows(result != 1, "修改失败,该数据可能已经被其他人修改,请刷新重试");
            return t;
        });
    }
}

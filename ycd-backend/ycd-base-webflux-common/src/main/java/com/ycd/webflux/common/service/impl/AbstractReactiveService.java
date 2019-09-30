package com.ycd.webflux.common.service.impl;



import com.ycd.common.entity.Entity;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.interfaces.ReactiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public abstract class AbstractReactiveService<ID, T extends Entity<ID>> implements ReactiveService<ID, T> {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected Mapper<T> mapper;


    @Override
    @WebFluxTransactional
    public Mono<ID> save(T t) {
        return Mono.fromSupplier(() -> {
            if (SimpleUtil.isEmpty(t)) {
                return null;
            }
            int count = mapper.insertSelective(t);
            SimpleUtil.trueAndThrows(count != 1, "保存用户失败");
            return t.id();
        });


    }

    @Override
    @WebFluxTransactional
    public Flux<ID> saveList(List<T> list) {
        return Flux.fromIterable(list)
                .flatMap(t -> save(t).flux());
    }

    @Override
    @WebFluxTransactional
    public Mono<T> update(T t) {
        return Mono.fromSupplier(() -> {
            int result = mapper.updateByPrimaryKeySelective(t);
            SimpleUtil.trueAndThrows(result != 1, "修改失败,该数据可能已经被其他人修改,请刷新重试");
            return t;
        });
    }

    @Override
    @WebFluxTransactional
    public Flux<T> updateList(List<T> list) {
        return Flux.fromIterable(list)
                .flatMap(t -> update(t).flux());
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteById(ID id) {
        return Mono.fromRunnable(() -> {
            int count = mapper.deleteByPrimaryKey(id);
            SimpleUtil.trueAndThrows(count != 1, "删除数据失败");
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteByIds(List<ID> ids) {
        if (SimpleUtil.isEmpty(ids)) {
            return Mono.empty();
        }
        return Flux.fromIterable(ids)
                .flatMap(id -> deleteById(id).flux())
                .then();
    }

    @Override
    public Mono<T> findById(ID id) {
        return Mono.justOrEmpty(id)
                .map(t -> mapper.selectByPrimaryKey(t));
    }
}

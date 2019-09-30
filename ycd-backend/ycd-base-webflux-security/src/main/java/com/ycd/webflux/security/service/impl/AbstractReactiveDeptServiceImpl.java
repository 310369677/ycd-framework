package com.ycd.webflux.security.service.impl;


import com.ycd.common.entity.security.Dept;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.impl.AbstractReactiveServiceWithCreateEntity;
import com.ycd.webflux.security.service.interfaces.ReactiveDeptService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-06-02
 */
public class AbstractReactiveDeptServiceImpl<T extends Dept> extends AbstractReactiveServiceWithCreateEntity<T> implements ReactiveDeptService<T> {
    @Override
    @WebFluxTransactional
    public Mono<Long> saveDept(T t) {
        return Mono.defer(() -> {
            SimpleUtil.assertNotEmpty(t.getDeptName(), "部门名字不能为空");
            //校验父级部门必须存在
            if (SimpleUtil.isNotEmpty(t.getParentId())) {
                SimpleUtil.assertNotEmpty(mapper.selectByPrimaryKey(t.getParentId()), "无效的父级id");
            }
            return save(t);
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> updateDept(T t) {
        return Mono.defer(() -> {
            SimpleUtil.assertNotEmpty(t.getId(), "部门的id不能为空");
            t.setParentId(null);
            return update(t).switchIfEmpty(Mono.error(SimpleUtil.newBusinessException("保存部门失败")))
                    .then();
        });

    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteDeptByIds(String ids) {
        if (SimpleUtil.isEmpty(ids)) {
            return Mono.empty();
        }
        return Flux.fromArray(ids.split(","))
                .map(id -> {
                    int count = mapper.deleteByPrimaryKey(Long.valueOf(id));
                    //TODO 删除部门时对应的权限怎么处理?
                    return count == 1;
                }).reduce((t1, t2) -> t1 && t2).then();
    }
}

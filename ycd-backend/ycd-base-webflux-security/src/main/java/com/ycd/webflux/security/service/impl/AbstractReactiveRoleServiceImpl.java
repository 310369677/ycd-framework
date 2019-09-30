package com.ycd.webflux.security.service.impl;



import com.ycd.common.entity.security.Role;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.impl.AbstractReactiveServiceWithCreateEntity;
import com.ycd.webflux.common.util.MonoUtil;
import com.ycd.webflux.security.service.interfaces.ReactiveRoleService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AbstractReactiveRoleServiceImpl<T extends Role> extends AbstractReactiveServiceWithCreateEntity<T> implements ReactiveRoleService<T> {

    @Override
    @WebFluxTransactional
    public Mono<Long> saveRole(T t) {
        return Mono.defer(() -> {
            SimpleUtil.assertNotEmpty(t.getName(), "角色名字不能为空");
            return save(t).switchIfEmpty(MonoUtil.businessError("保存角色失败"));
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> updateRole(T t) {
        return Mono.defer(() -> {
            SimpleUtil.assertNotEmpty(t.getId(), "id不能为空");
            SimpleUtil.assertNotEmpty(t.getName(), "角色名字不能为空");
            return update(t)
                    .switchIfEmpty(MonoUtil.businessError("修改角色失败")).then();
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteRolesByIds(String ids) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        return Flux.fromArray(ids.split(","))
                .map(id -> {
                    int count = mapper.deleteByPrimaryKey(Long.valueOf(id));
                    //TODO 删除角色时，权限以及关联到对应角色的用户的关联关系?
                    return count == 1;
                }).reduce((t1, t2) -> t1 && t2).then();
    }
}

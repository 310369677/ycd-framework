package com.ycd.webflux.security.service.interfaces;


import com.ycd.common.entity.security.Role;
import com.ycd.webflux.common.service.interfaces.LongPriReactiveService;
import reactor.core.publisher.Mono;

public interface ReactiveRoleService<T extends Role> extends LongPriReactiveService<T> {


    Mono<Long> saveRole(T t);

    Mono<Void> updateRole(T t);

    Mono<Void> deleteRolesByIds(String ids);
}

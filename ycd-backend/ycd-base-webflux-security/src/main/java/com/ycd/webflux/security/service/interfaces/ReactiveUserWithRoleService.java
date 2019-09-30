package com.ycd.webflux.security.service.interfaces;


import com.ycd.common.entity.security.UserWithRole;
import com.ycd.webflux.common.service.interfaces.LongPriReactiveService;
import reactor.core.publisher.Mono;

public interface ReactiveUserWithRoleService<T extends UserWithRole> extends LongPriReactiveService<T> {


    Mono<Void> relationUserAndRole(String userId, String roleId);

    Mono<Void> relationUserAndRoles(String userId, String roleIds);
}

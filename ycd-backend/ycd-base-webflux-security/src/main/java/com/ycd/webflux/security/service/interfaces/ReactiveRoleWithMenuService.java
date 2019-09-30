package com.ycd.webflux.security.service.interfaces;


import com.ycd.common.entity.security.RoleWithMenu;
import com.ycd.webflux.common.service.interfaces.LongPriReactiveService;
import reactor.core.publisher.Mono;

public interface ReactiveRoleWithMenuService<T extends RoleWithMenu> extends LongPriReactiveService<T> {

    Mono<Void> relationRoleAndMenu(String roleId, String menuId);

    Mono<Void> relationRoleAndMenus(String roleId, String menuIds);
}

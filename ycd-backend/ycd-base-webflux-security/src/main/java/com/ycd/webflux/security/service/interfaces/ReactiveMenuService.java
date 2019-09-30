package com.ycd.webflux.security.service.interfaces;


import com.ycd.common.entity.security.Menu;
import com.ycd.webflux.common.service.interfaces.LongPriReactiveService;
import reactor.core.publisher.Mono;

public interface ReactiveMenuService<T extends Menu> extends LongPriReactiveService<T> {
    /**
     * 保存菜单
     *
     * @param menu 菜单
     * @return 保存的菜单id
     */
    Mono<Long> saveMenu(T menu);

    /**
     * 修改菜单
     *
     * @param menu 菜单
     */
    Mono<Void> updateMenu(T menu);

    Mono<Void> deleteMenu(String menuIds);
}

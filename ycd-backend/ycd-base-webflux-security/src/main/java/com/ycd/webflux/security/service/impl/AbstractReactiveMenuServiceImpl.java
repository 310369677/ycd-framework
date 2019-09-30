package com.ycd.webflux.security.service.impl;


import com.ycd.common.entity.security.Menu;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.impl.AbstractReactiveServiceWithCreateEntity;
import com.ycd.webflux.security.service.interfaces.ReactiveMenuService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class AbstractReactiveMenuServiceImpl<T extends Menu> extends AbstractReactiveServiceWithCreateEntity<T> implements ReactiveMenuService<T> {
    @Override
    @WebFluxTransactional
    public Mono<Long> saveMenu(T menu) {
        return save(menu).switchIfEmpty(Mono.error(SimpleUtil.newBusinessException("保存菜单失败")));
    }


    @Override
    @WebFluxTransactional
    public Mono<Void> updateMenu(T menu) {
        return Mono.defer(() -> {
            //仅仅允许修改名字，或者其他的信息，不能修改parentId
            SimpleUtil.assertNotEmpty(menu.getId(), "menuId不能为空");

            //TODO 暂时不许修改parentId,如有需求再进行修正
            menu.setParentId(null);
            return update(menu).switchIfEmpty(Mono.error(SimpleUtil.newBusinessException("保存菜单失败"))).then();
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteMenu(String menuIds) {
        return Mono.defer(() -> {
            SimpleUtil.assertNotEmpty(menuIds, "menuIds不能为空");
            List<Long> idList = new ArrayList<>();
            for (String menuId : menuIds.split(",")) {
                idList.add(Long.parseLong(menuId));
                //TODO 删除与这个菜单相关的权限
            }
            return deleteByIds(idList);
        });
    }
}

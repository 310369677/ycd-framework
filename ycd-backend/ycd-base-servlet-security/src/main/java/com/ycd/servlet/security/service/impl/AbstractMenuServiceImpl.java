package com.ycd.servlet.security.service.impl;


import com.ycd.common.entity.security.Menu;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.impl.AbstractServiceWithCreateEntity;
import com.ycd.servlet.security.service.interfaces.MenuService;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public class AbstractMenuServiceImpl<T extends Menu> extends AbstractServiceWithCreateEntity<T> implements MenuService<T> {
    @Override
    @Transactional
    public Long saveMenu(T menu) {
        return save(menu);
    }

    @Override
    @Transactional
    public void updateMenu(T menu) {
        //仅仅允许修改名字，或者其他的信息，不能修改parentId
        SimpleUtil.assertNotEmpty(menu.getId(), "menuId不能为空");

        //TODO 暂时不许修改parentId,如有需求再进行修正
        menu.setParentId(null);
        update(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(String menuIds) {
        SimpleUtil.assertNotEmpty(menuIds, "menuIds不能为空");
        Stream.of(menuIds.split(",")).map(Long::valueOf)
                .forEach(this::deleteById);
    }
}

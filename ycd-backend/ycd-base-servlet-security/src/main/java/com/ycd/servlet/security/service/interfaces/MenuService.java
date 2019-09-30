package com.ycd.servlet.security.service.interfaces;


import com.ycd.common.entity.security.Menu;
import com.ycd.servlet.common.service.interfaces.LongPriService;

public interface MenuService<T extends Menu> extends LongPriService<T> {

    /**
     * 保存菜单
     *
     * @param menu 菜单
     * @return 保存的菜单id
     */
    Long saveMenu(T menu);

    /**
     * 修改菜单
     *
     * @param menu 菜单
     */
    void updateMenu(T menu);

    void deleteMenu(String menuIds);
}

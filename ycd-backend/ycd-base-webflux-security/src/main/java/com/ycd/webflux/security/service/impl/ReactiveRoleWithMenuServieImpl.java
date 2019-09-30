package com.ycd.webflux.security.service.impl;


import com.ycd.common.entity.security.RoleWithMenu;
import com.ycd.common.repo.mybatis.security.DefaultMenuMapper;
import com.ycd.common.repo.mybatis.security.DefaultRoleMapper;
import com.ycd.common.repo.mybatis.security.RoleMenuMapper;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.impl.AbstractReactiveServiceWithCreateEntity;
import com.ycd.webflux.security.service.interfaces.ReactiveRoleWithMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveRoleWithMenuServieImpl extends AbstractReactiveServiceWithCreateEntity<RoleWithMenu> implements ReactiveRoleWithMenuService<RoleWithMenu> {


    @Autowired
    RoleMenuMapper roleMenuMapper;

    @Autowired
    DefaultRoleMapper roleMapper;

    @Autowired
    DefaultMenuMapper menuMapper;

    @Override
    @WebFluxTransactional
    public Mono<Void> relationRoleAndMenu(String roleId, String menuId) {
        return Mono.fromRunnable(() -> {
            if (SimpleUtil.isEmpty(roleId) || SimpleUtil.isEmpty(menuId)) {
                return;
            }
            RoleWithMenu roleWithMenu = new RoleWithMenu();
            roleWithMenu.setRoleId(roleId);
            roleWithMenu.setMenuId(menuId);
            RoleWithMenu dataBase = roleMenuMapper.selectOne(roleWithMenu);
            if (SimpleUtil.isNotEmpty(dataBase)) {
                return;
            }
            //判断角色和菜单必须存在
            SimpleUtil.assertNotEmpty(roleMapper.selectByPrimaryKey(Long.valueOf(roleId)), "无效的角色id");
            SimpleUtil.assertNotEmpty(menuMapper.selectByPrimaryKey(Long.valueOf(menuId)), "无效的菜单id");
            int count = roleMenuMapper.insertSelective(roleWithMenu);
            LOGGER.debug("角色id:{},菜单id:{}关联成功", roleId, menuId);
            SimpleUtil.trueAndThrows(count != 1, "关联角色和菜单失败");
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> relationRoleAndMenus(String roleId, String menuIds) {
        return Mono.defer(() -> {
            if (SimpleUtil.isEmpty(roleId) || SimpleUtil.isEmpty(menuIds)) {
                return Mono.empty();
            }
            return Flux.fromArray(menuIds.split(","))
                    .flatMap(menuId -> relationRoleAndMenu(roleId, menuId)).then();
        });
    }
}

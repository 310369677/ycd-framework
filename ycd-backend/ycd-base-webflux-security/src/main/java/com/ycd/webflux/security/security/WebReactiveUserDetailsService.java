package com.ycd.webflux.security.security;



import com.ycd.common.entity.User;
import com.ycd.common.entity.security.Menu;
import com.ycd.common.entity.security.Role;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.security.service.interfaces.ReactiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class WebReactiveUserDetailsService implements ReactiveUserDetailsService {


    private static final String LOCK_STATUS = "2";

    /**
     * 定义角色
     */
    public static List<Role> ALL_ROLES = new ArrayList();
    public static List<Role> ADMIN_ROLES = new ArrayList();
    public static List<Role> COMMON_ROLES = new ArrayList();
    /**
     * 定义权限
     */
    public static List<Menu> ADMIN_MENUS = new ArrayList();
    public static List<Menu> COMMON_MENUS = new ArrayList();

    static {
        //定义菜单
       /* Menu commonMenu1 = new Menu(1, "首页", "/security/common", 0);
        Menu commonMenu2 = new Menu(2, "服务", "/security/service", 0);
        Menu commonMenu3 = new Menu(3, "公司", "/security/company", 0);
        Menu adminMenu = new Menu(4, "系统管理", "/security/manager", 0);*/
        //初始化菜单
      /*  ADMIN_MENUS.add(adminMenu);
        ADMIN_MENUS.add(commonMenu1);
        ADMIN_MENUS.add(commonMenu2);
        ADMIN_MENUS.add(commonMenu3);
        COMMON_MENUS.add(commonMenu1);
        COMMON_MENUS.add(commonMenu2);
        COMMON_MENUS.add(commonMenu3);*/
        //定义角色
        Role adminRole = new Role(1L, "admin");
        Role commonRole = new Role(2L, "common");
        adminRole.setMenus(ADMIN_MENUS);
        commonRole.setMenus(COMMON_MENUS);
        //初始化角色
        ADMIN_ROLES.add(adminRole);
        ADMIN_ROLES.add(commonRole);
        COMMON_ROLES.add(commonRole);
        ALL_ROLES.add(adminRole);
        ALL_ROLES.add(commonRole);
    }


    @Autowired
    private ReactiveUserService reactiveUserService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {


        User user = null;
        try {
            user = (User) reactiveUserService.findUserByUserName(username).toFuture().get();
        } catch (Exception e) {
            throw SimpleUtil.newBusinessException("获取用户失败");
        }
        SimpleUtil.assertNotEmpty(user, "用户名不存在");
        SimpleUtil.trueAndThrows(LOCK_STATUS.equals(user.getStatus()), "用户已经被锁定");
        switch (username) {
            case "admin":
                user.setRoleList(ADMIN_ROLES);
            default:
                user.setRoleList(COMMON_ROLES);
        }
        return Mono.just(new SecurityUserDetails(user));
    }
}

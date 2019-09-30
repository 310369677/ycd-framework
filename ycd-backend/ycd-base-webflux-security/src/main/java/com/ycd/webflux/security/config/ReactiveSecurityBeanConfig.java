package com.ycd.webflux.security.config;


import com.ycd.common.entity.security.DefaultDept;
import com.ycd.common.entity.security.DefaultMenu;
import com.ycd.common.entity.security.DefaultRole;
import com.ycd.common.entity.security.DefaultUser;
import com.ycd.webflux.security.security.*;
import com.ycd.webflux.security.service.impl.*;
import com.ycd.webflux.security.service.interfaces.*;
import com.ycd.webflux.security.web.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveSecurityBeanConfig {

    @Bean
    @ConditionalOnMissingBean(ReactiveDeptController.class)
    public ReactiveDeptController<DefaultDept> deptController() {
        return new DefaultReactiveDeptController();
    }


    @Bean
    @ConditionalOnMissingBean(ReactiveDeptService.class)
    public ReactiveDeptService deptService() {
        return new DefaultReactiveDeptServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveMenuController.class)
    public ReactiveMenuController<DefaultMenu> menuController() {
        return new DefaultReactiveMenuController();
    }


    @Bean
    @ConditionalOnMissingBean(ReactiveMenuService.class)
    public ReactiveMenuService menuService() {
        return new DefaultReactiveMenuServiceImpl();
    }


    @Bean
    @ConditionalOnMissingBean(ReactiveRoleController.class)
    public ReactiveRoleController<DefaultRole> roleController() {
        return new DefaultReactiveRoleController();
    }


    @Bean
    @ConditionalOnMissingBean(ReactiveRoleService.class)
    public ReactiveRoleService roleService() {
        return new DefaultReactiveRoleServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveUserController.class)
    public ReactiveUserController<DefaultUser> userController() {
        return new DefaultReactiveUserController();
    }


    @Bean
    @ConditionalOnMissingBean(ReactiveUserService.class)
    public ReactiveUserService userService() {
        return new DefaultReactiveUserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveRoleWithMenuService.class)
    public ReactiveRoleWithMenuService roleWithMenuService() {
        return new ReactiveRoleWithMenuServieImpl();
    }


    @Bean
    @ConditionalOnMissingBean(ReactiveUserWithRoleService.class)
    public ReactiveUserWithRoleService userWithRoleService() {
        return new ReactiveUserWithRoleServiceImpl();
    }


    //配置bean
    @Bean
    public ServerAccessDeniedHandler authAccessDeniedHandler() {
        return new AuthAccessDeniedHandler();
    }

    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return new AuthEnteryPoint();
    }


    @Bean
    public ServerAuthenticationFailureHandler serverAuthenticationFailureHandler() {
        return new AuthFailureHandler();
    }


    @Bean
    public ServerAuthenticationSuccessHandler serverAuthenticationSuccessHandler() {
        return new AuthSuccessHandler();
    }

    @Bean
    public ServerLogoutSuccessHandler serverLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }


    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository() {
        return new TokenServerSecurityContextRepository();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return new WebReactiveUserDetailsService();
    }


}

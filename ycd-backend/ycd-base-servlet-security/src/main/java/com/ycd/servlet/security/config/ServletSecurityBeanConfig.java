package com.ycd.servlet.security.config;


import com.ycd.servlet.security.security.*;
import com.ycd.servlet.security.service.impl.DefaultDeptServiceImpl;
import com.ycd.servlet.security.service.impl.DefaultMenuServiceImpl;
import com.ycd.servlet.security.service.impl.DefaultRoleServiceImpl;
import com.ycd.servlet.security.service.impl.DefaultUserServiceImpl;
import com.ycd.servlet.security.service.interfaces.DeptService;
import com.ycd.servlet.security.service.interfaces.MenuService;
import com.ycd.servlet.security.service.interfaces.RoleService;
import com.ycd.servlet.security.service.interfaces.UserService;
import com.ycd.servlet.security.web.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletSecurityBeanConfig {


    @Bean
    public CodeController codeController() {
        return new CodeController();
    }


    @Bean
    @ConditionalOnMissingBean(UserController.class)
    public UserController userController() {
        return new DefaultUserController();
    }

    @Bean
    @ConditionalOnMissingBean(DeptController.class)
    public DeptController deptController() {
        return new DefaultDeptController();
    }

    @Bean
    @ConditionalOnMissingBean(RoleController.class)
    public RoleController roleController() {
        return new DefaultRoleController();
    }

    @Bean
    @ConditionalOnMissingBean(MenuController.class)
    public MenuController menuController() {
        return new DefaultMenuController();
    }


    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService userService() {
        return new DefaultUserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(RoleService.class)
    public RoleService roleService() {
        return new DefaultRoleServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(MenuService.class)
    public MenuService menuService() {
        return new DefaultMenuServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(DeptService.class)
    public DeptService deptService() {
        return new DefaultDeptServiceImpl();
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandlerImpl();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandlerImpl();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new TokenSecurityContextRepository();
    }




    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1");
    }
}

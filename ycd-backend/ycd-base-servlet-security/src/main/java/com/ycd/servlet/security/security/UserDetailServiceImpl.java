package com.ycd.servlet.security.security;


import com.ycd.common.config.security.SecurityConfig;
import com.ycd.common.entity.User;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.security.entity.SecurityUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;

public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private com.ycd.servlet.security.service.interfaces.UserService UserService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SecurityConfig securityConfig;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //如果验证码打开的话，就先验证验证码
        if (securityConfig.isOpenCode()) {
            //验证
            String code = request.getParameter("code");
            SimpleUtil.assertNotEmpty(code, "验证码不能为空");
            String sessionCode = (String) request.getSession().getAttribute("code");
            SimpleUtil.trueAndThrows(!code.equals(sessionCode), "验证码错误");
        }
        User user = UserService.loadUserByUsername(username);
        return new SecurityUserDetails(user);
    }
}

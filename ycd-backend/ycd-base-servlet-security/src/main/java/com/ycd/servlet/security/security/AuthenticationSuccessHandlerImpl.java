package com.ycd.servlet.security.security;


import com.ycd.common.Result;
import com.ycd.servlet.common.cache.RedisUserCache;
import com.ycd.servlet.common.util.HttpServletResponseUtil;
import com.ycd.servlet.security.entity.SecurityUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    RedisUserCache redisUserCache;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) authentication.getPrincipal();
        //生成token TODO 具体生成token一会实现
        String token = redisUserCache.cache(securityUserDetails);
        HttpServletResponseUtil.writeEntityInfo(response, Result.ok(token));
    }
}

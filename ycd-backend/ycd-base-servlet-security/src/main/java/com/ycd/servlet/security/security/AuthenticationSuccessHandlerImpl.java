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
import java.util.HashMap;
import java.util.Map;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    RedisUserCache redisUserCache;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) authentication.getPrincipal();
        //生成token
        String token = redisUserCache.cache(securityUserDetails);
        Map<String, Object> result = new HashMap<>();
        result.putIfAbsent("token", token);
        securityUserDetails.setPassword(null);
        result.putIfAbsent("user", securityUserDetails);
        HttpServletResponseUtil.writeEntityInfo(response, Result.ok(result));
    }
}

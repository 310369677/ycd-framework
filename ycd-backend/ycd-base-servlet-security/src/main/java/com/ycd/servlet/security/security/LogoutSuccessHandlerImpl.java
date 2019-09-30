package com.ycd.servlet.security.security;


import com.ycd.common.Result;
import com.ycd.common.config.CommonConfig;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.cache.UserCacheProxy;
import com.ycd.servlet.common.util.HttpServletResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    UserCacheProxy userCacheProxy;

    @Autowired
    CommonConfig config;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //获取当前的token
        //获取token
        String token = request.getHeader(config.getTokenAuthHead());
        if (SimpleUtil.isEmpty(token)) {
            return;
        }
        userCacheProxy.clearCacheByToken(token);
        //写入成功的结果信息
        HttpServletResponseUtil.writeEntityInfo(response, Result.ok());
    }
}

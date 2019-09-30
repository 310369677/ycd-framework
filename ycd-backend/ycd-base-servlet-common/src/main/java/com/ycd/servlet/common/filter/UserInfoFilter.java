package com.ycd.servlet.common.filter;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.context.UserContext;
import com.ycd.common.entity.User;
import com.ycd.common.exception.TokenInvalidException;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.cache.RedisUserCache;
import com.ycd.servlet.common.util.HttpServletResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class UserInfoFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoFilter.class);

    CommonConfig config;

    RedisUserCache redisUserCache;

    private static final String USER_LOCK = "2";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        //获取token
        String token = req.getHeader(config.getTokenAuthHead());
        if (SimpleUtil.isEmpty(token)) {
            logger.debug("请求{}没有携带token", ((HttpServletRequest) request).getRequestURI());
            chain.doFilter(request, response);
            return;
        }
        //开始解析token
        logger.debug("请求{}开始被解析[token：{}]", ((HttpServletRequest) request).getRequestURI(), token);
        User user = redisUserCache.userInfo(token);
        if (SimpleUtil.isEmpty(user)) {
            HttpServletResponseUtil.writeException((HttpServletResponse) response, new TokenInvalidException("无效token"));
            return;
        }
        //判断user是否被锁定
        UserContext.setUser(user);
        SimpleUtil.trueAndThrows(USER_LOCK.equals(user.getStatus()), "用户被锁定");
        redisUserCache.refreshTokenExpireTime(token);
        chain.doFilter(request, response);
    }

    public CommonConfig getConfig() {
        return config;
    }

    public void setConfig(CommonConfig config) {
        this.config = config;
    }

    public RedisUserCache getRedisUserCache() {
        return redisUserCache;
    }

    public void setRedisUserCache(RedisUserCache redisUserCache) {
        this.redisUserCache = redisUserCache;
    }
}

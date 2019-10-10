package com.ycd.webflux.security.security;

import com.ycd.common.Result;
import com.ycd.common.config.CommonConfig;
import com.ycd.webflux.common.cache.ReactiveRedisUserCache;
import com.ycd.webflux.common.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;


public class CustomLogoutSuccessHandler implements ServerLogoutSuccessHandler {


    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    @Autowired
    CommonConfig commonConfig;

    @Autowired
    ReactiveRedisUserCache userCache;

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        ServerHttpResponse response = exchange.getExchange().getResponse();

            //清空上下文，并且让token失效
            SecurityContextHolder.clearContext();
            //让token失效,获取token
        String token = exchange.getExchange().getRequest().getHeaders().getFirst(commonConfig.getTokenAuthHead());
            return userCache.clearCacheByToken(token).flatMap(b -> {
                logger.debug("token:{}清空", token);
                //回写成功的信息
                return ResponseUtil.writeEntityInfo(response, Result.ok("退出登录成功"));
            }).onErrorResume(e->ResponseUtil.writeException(response,e));
    }
}

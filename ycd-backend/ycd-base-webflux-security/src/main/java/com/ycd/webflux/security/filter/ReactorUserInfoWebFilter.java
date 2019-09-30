package com.ycd.webflux.security.filter;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.context.UserContext;
import com.ycd.common.entity.User;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.cache.ReactiveRedisUserCache;
import com.ycd.webflux.common.context.ReactiveUserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.util.List;

/**
 * 作者:杨川东
 * 日期:2019-06-09
 */

public class ReactorUserInfoWebFilter implements WebFilter {


    private static final Logger log = LoggerFactory.getLogger(ReactorUserInfoWebFilter.class);


    CommonConfig commonConfig;

    ReactiveRedisUserCache reactiveRedisUserCache;

    private static final String USER_LOCK = "2";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .subscriberContext(c -> c.hasKey(SecurityContext.class) ? c :
                        withUserContext(c, exchange)
                );
    }


    private Context withUserContext(Context mainContext, ServerWebExchange exchange) {
        return mainContext.putAll(loadUserInfo(exchange)
                .as(ReactiveUserContextHolder::withUserMono));
    }

    private Mono<User> loadUserInfo(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        SecurityContextHolder.clearContext();
        List<String> tokens = request.getHeaders().get(commonConfig.getTokenAuthHead());
        if (SimpleUtil.isEmpty(tokens)) {
            return Mono.empty();
        }
        log.info("获取token当前执行的线程是{}", Thread.currentThread().getName());
        String token = tokens.get(0);
        Mono<User> userM = reactiveRedisUserCache.userInfo(token);
        User user = null;
        try {
            user = userM.toFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SimpleUtil.assertNotEmpty(user, "无效token");
        //判断user是否被锁定
        UserContext.setUser(user);
        SimpleUtil.trueAndThrows(USER_LOCK.equals(user.getStatus()), "用户被锁定");
        //刷新token
        return reactiveRedisUserCache.refreshTokenExpireTime(token).zipWith(Mono.just(user))
                .flatMap(tp2 -> Mono.just(tp2.getT2()));
    }

    public void setCommonConfig(CommonConfig config) {
        this.commonConfig = config;
    }

    public void setReactiveRedisUserCache(ReactiveRedisUserCache reactiveRedisUserCache) {
        this.reactiveRedisUserCache = reactiveRedisUserCache;
    }
}

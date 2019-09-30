package com.ycd.webflux.common.cache;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.Duration;


public class UserCacheProxyReactive implements ReactiveRedisUserCache {

    @Autowired
    private InnerReactiveUserSingleLoginCache userSingleLoginCache;

    @Autowired
    private InnerReactiveUserMultiLoginCache userMultiLoginCache;

    @Autowired
    CommonConfig config;


    public Mono<Boolean> refreshCacheUserInfo(User user) {
        long expireTime = config.getUserLoginExpireTime();
        InnerReactiveRedisUserCache reactiveUserCache = getRealCache();
        Duration duration = expireTime > 0 ? Duration.ofSeconds(expireTime) : null;
        return reactiveUserCache.refreshCacheUserInfo(user, duration);
    }


    @Override
    public Mono<String> cache(User user) {
        long expireTime = config.getUserLoginExpireTime();
        Duration duration = expireTime > 0 ? Duration.ofSeconds(expireTime) : null;
        return getRealCache().cache(user, duration);
    }

    @Override
    public Mono<Boolean> clearCache(User user) {
        return getRealCache().clearCache(user);
    }

    @Override
    public Mono<Boolean> clearCacheByToken(String token) {
        return getRealCache().clearCacheByToken(token);
    }

    @Override
    public Mono<Boolean> refreshTokenExpireTime(String result) {
        long expireTime = config.getUserLoginExpireTime();
        Duration duration = expireTime > 0 ? Duration.ofSeconds(expireTime) : null;
        return getRealCache().refreshTokenExpireTime(result, duration);
    }

    @Override
    public Mono<User> userInfo(String token) {
        return getRealCache().userInfo(token);
    }


    private InnerReactiveRedisUserCache getRealCache() {
        if ("1".equals(config.getUserLoginModel())) {
            return userSingleLoginCache;
        } else if ("2".equals(config.getUserLoginModel())) {
            return userMultiLoginCache;
        }
        throw new UnsupportedOperationException(String.format("不支持的登录模式:%S", config.getUserLoginModel()));
    }
}

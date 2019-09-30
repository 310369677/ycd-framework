package com.ycd.webflux.common.cache;


import com.ycd.common.entity.User;
import reactor.core.publisher.Mono;

public interface ReactiveRedisUserCache {

    Mono<Boolean> refreshCacheUserInfo(User user);


    Mono<String> cache(User user);


    Mono<User> userInfo(String token);

    Mono<Boolean> clearCache(User user);

    Mono<Boolean> refreshTokenExpireTime(String token);

    Mono<Boolean> clearCacheByToken(String token);
}

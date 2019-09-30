package com.ycd.webflux.common.cache;



import com.ycd.common.entity.User;
import reactor.core.publisher.Mono;

import java.time.Duration;

interface InnerReactiveRedisUserCache {


    Mono<String> cache(User user);


    Mono<User> userInfo(String token);

    Mono<Boolean> clearCache(User user);

    Mono<Boolean> clearCacheByToken(String token);

    Mono<Boolean> refreshTokenExpireTime(String token);

    Mono<String> cache(User user, Duration expire);

    Mono<Boolean> refreshCacheUserInfo(User user, Duration expire);


    Mono<Boolean> refreshTokenExpireTime(String token, Duration expire);
}

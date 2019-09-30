package com.ycd.webflux.common.cache;

import com.alibaba.fastjson.JSON;
import com.ycd.common.entity.User;
import com.ycd.common.util.SimpleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.UUID;


public class InnerReactiveUserSingleLoginCache implements InnerReactiveRedisUserCache {

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(InnerReactiveUserSingleLoginCache.class);

    /**
     * 缓存用户
     *
     * @param user 用户
     * @return 结果
     */
    public Mono<String> cache(User user, Duration expireTime) {
        String key = getKey(user);
        String uuid = uuid();
        Mono<String> result = Mono.just(uuid);
        result = result.flatMap(uid -> {
            LOGGER.debug("cache 方法开始删除token{}相关信息", uid);
            Mono<String> m = reactiveRedisTemplate.opsForValue().get(key);
            m = m.zipWhen(tk -> Mono.zip(reactiveRedisTemplate.opsForValue().delete(key), reactiveRedisTemplate.opsForValue().delete(tk)).flatMap(tp2 -> Mono.just(tp2.getT1() && tp2.getT2())))
                    .flatMap(tp2 -> Mono.just(uid));
            return m;
        });
        result = result.switchIfEmpty(Mono.just(uuid));
        //对结果进行处理
        return result.flatMap(uid -> {
            Mono<Duration> durationMono = Mono.justOrEmpty(expireTime);
            Mono<String> m = durationMono.zipWhen(time -> Mono.zip(reactiveRedisTemplate.opsForValue().set(key, uid, time),
                    reactiveRedisTemplate.opsForValue().set(uid, JSON.toJSONString(user), time))).flatMap(tp2 -> Mono.just(uid));
            return m.switchIfEmpty(Mono.zip(reactiveRedisTemplate.opsForValue().set(key, uid),
                    reactiveRedisTemplate.opsForValue().set(uid, JSON.toJSONString(user))).flatMap(tp2 -> Mono.just(uid)));
        });
    }

    @Override
    public Mono<Boolean> refreshCacheUserInfo(User user, Duration expire) {
        String key = getKey(user);
        return reactiveRedisTemplate.opsForValue().get(key)
                .flatMap(uuid -> {
                    if (SimpleUtil.isNotEmpty(expire)) {
                        return reactiveRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(user), expire);
                    }
                    return reactiveRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(user));
                })
                .defaultIfEmpty(false);
    }


    private String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private String getKey(User user) {
        SimpleUtil.assertNotEmpty(user, "用户不能为空");
        SimpleUtil.assertNotEmpty(user.getId(), "用户id不能为空");
        String prefix = "__single_user_prefix__";
        return prefix + user.getId();
    }


    @Override
    public Mono<String> cache(User user) {
        return cache(user, null);
    }

    @Override
    public Mono<Boolean> clearCache(User user) {
        String key = getKey(user);
        Mono<String> token = reactiveRedisTemplate.opsForValue().get(key);
        Mono<Boolean> result = token.flatMap(this::clearCacheByToken);
        return result.switchIfEmpty(Mono.just(true));
    }

    @Override
    public Mono<Boolean> refreshTokenExpireTime(String token, Duration expire) {

        Mono<String> tokenMono = reactiveRedisTemplate.opsForValue().get(token);
        return tokenMono.flatMap(userStr -> {
            User user = JSON.parseObject(userStr, User.class);
            String key = getKey(user);
            Mono<Duration> durationMono = Mono.justOrEmpty(expire);
            Mono<Boolean> result = durationMono.zipWhen(expire1 -> Mono.zip(reactiveRedisTemplate.opsForValue().set(key, token, expire1), reactiveRedisTemplate.opsForValue().set(token, userStr, expire1))
                    .flatMap(tp2 -> Mono.just(tp2.getT1() && tp2.getT2()))).flatMap(tp2 -> Mono.just(tp2.getT2()));
            return result.switchIfEmpty(Mono.zip(reactiveRedisTemplate.opsForValue().set(key, token), reactiveRedisTemplate.opsForValue().set(token, userStr))
                    .flatMap(tp2 -> Mono.just(tp2.getT1() && tp2.getT2())));
        }).switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<Boolean> clearCacheByToken(String token) {
        Mono<String> userStrMono = reactiveRedisTemplate.opsForValue().get(token);
        return userStrMono.flatMap(userStr -> {
            User user = JSON.parseObject(userStr, User.class);
            String key = getKey(user);
            return Mono.zip(reactiveRedisTemplate.opsForValue().delete(key), reactiveRedisTemplate.opsForValue().delete(token))
                    .flatMap(tp2 -> Mono.just(tp2.getT1() && tp2.getT2()));
        }).switchIfEmpty(Mono.just(true));
    }

    @Override
    public Mono<Boolean> refreshTokenExpireTime(String token) {
        return refreshTokenExpireTime(token, null);
    }

    @Override
    public Mono<User> userInfo(String token) {
        return (reactiveRedisTemplate.opsForValue().get(token)
                .flatMap(val -> Mono.just(JSON.parseObject(val, User.class))));
    }
}

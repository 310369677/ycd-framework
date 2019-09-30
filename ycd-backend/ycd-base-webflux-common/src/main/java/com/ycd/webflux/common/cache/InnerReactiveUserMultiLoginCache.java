package com.ycd.webflux.common.cache;

import com.alibaba.fastjson.JSON;
import com.ycd.common.entity.User;
import com.ycd.common.util.IDUtil;
import com.ycd.common.util.SimpleUtil;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Random;


public class InnerReactiveUserMultiLoginCache implements InnerReactiveRedisUserCache {


    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;


    private String prefix = "a";
    private String suffix = "a";
    private String sec = "20190529";

    @Override
    public Mono<String> cache(User user) {
        return cache(user, null);
    }

    @Override
    public Mono<String> cache(User user, Duration expire) {
        if (SimpleUtil.isEmpty(user) || SimpleUtil.isEmpty(user.getId())) {
            return Mono.empty();
        }
        String key = generateKey(user.getId());
        Mono<Duration> durationMono = Mono.justOrEmpty(expire);
        return durationMono.flatMap(expire1 -> reactiveRedisTemplate.opsForValue().set(key, JSON.toJSONString(user)))
                .filter(t -> t).flatMap(t -> Mono.just(key));
    }

    @Override
    public Mono<Boolean> refreshCacheUserInfo(User user, Duration expire) {
        return Mono.defer(() -> {
            if (SimpleUtil.isEmpty(user) || SimpleUtil.isEmpty(user.getId())) {
                return null;
            }
            String pattern = "*" + prefix + sec + user.getId() + suffix + "*";
            return reactiveRedisTemplate.keys(pattern)
                    .flatMap(key -> reactiveRedisTemplate.opsForValue().get(key)
                            .flatMap(val -> {
                                if (SimpleUtil.isEmpty(expire)) {
                                    return reactiveRedisTemplate.opsForValue().set(key, val, expire);
                                }
                                return reactiveRedisTemplate.opsForValue().set(key, val);
                            })).reduce((t1, t2) -> t1 && t2);
        });
    }


    private String generateKey(Long userId) {
        SimpleUtil.assertNotEmpty(userId, "用户id不能为空");
        String uuid = IDUtil.uuid();
        Random random = new Random();
        int index = random.nextInt(32);
        StringBuilder builder = new StringBuilder();
        builder.append(uuid, 0, index + 1).append(prefix).append(sec)
                .append(userId).append(suffix);
        if (index < 31) {
            builder.append(uuid.substring(index + 1));
        }
        return builder.toString();
    }

    @Override
    public Mono<Boolean> clearCache(User user) {
        if (SimpleUtil.isEmpty(user) || SimpleUtil.isEmpty(user.getId())) {
            return Mono.just(false);
        }
        String pattern = "*" + prefix + sec + user.getId() + suffix + "*";
        return reactiveRedisTemplate.delete(reactiveRedisTemplate.keys(pattern))
                .flatMap(count -> Mono.just(count > 0));
    }

    @Override
    public Mono<Boolean> clearCacheByToken(String token) {
        if (SimpleUtil.isEmpty(token)) {
            return Mono.just(false);
        }
        return reactiveRedisTemplate.opsForValue().delete(token);
    }

    @Override
    public Mono<Boolean> refreshTokenExpireTime(String token) {
        return refreshTokenExpireTime(token, null);
    }

    @Override
    public Mono<User> userInfo(String token) {
        Mono<String> userStrMono = reactiveRedisTemplate.opsForValue().get(token);
        return userStrMono.flatMap(userStr -> Mono.justOrEmpty(JSON.parseObject(userStr, User.class)));
    }


    @Override
    public Mono<Boolean> refreshTokenExpireTime(String token, Duration expire) {
        if (SimpleUtil.isEmpty(token)) {
            return Mono.just(false);
        }
        Mono<String> m = reactiveRedisTemplate.opsForValue().get(token);
        return m.flatMap(userVal -> {
            Mono<Duration> durationMono = Mono.justOrEmpty(expire);
            Mono<Boolean> result = durationMono.flatMap(expire1 -> reactiveRedisTemplate.opsForValue().set(token, userVal, expire1));
            return result.switchIfEmpty(reactiveRedisTemplate.opsForValue().set(token, userVal));
        }).switchIfEmpty(Mono.just(false));
    }

    public static void main(String[] args) {

    }


}

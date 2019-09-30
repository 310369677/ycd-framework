package com.ycd.servlet.common.cache;

import com.ycd.common.config.CommonConfig;
import com.ycd.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

public class UserCacheProxy implements RedisUserCache {


    @Autowired
    UserSingleLoginCache userSingleLoginCache;

    @Autowired
    UserMultiLoginCache userMultiLoginCache;

    @Autowired
    CommonConfig config;

    @Override
    public Boolean refreshCacheUserInfo(User user) {
        long expireTime = config.getUserLoginExpireTime();
        InnerRedisUserCache reactiveUserCache = getRealCache();
        Duration duration = expireTime > 0 ? Duration.ofSeconds(expireTime) : null;
        return reactiveUserCache.refreshCacheUserInfo(user, duration);
    }

    @Override
    public String cache(User user) {
        long expireTime = config.getUserLoginExpireTime();
        Duration duration = expireTime > 0 ? Duration.ofSeconds(expireTime) : null;
        return getRealCache().cache(user, duration);
    }

    @Override
    public User userInfo(String token) {
        return getRealCache().userInfo(token);
    }

    @Override
    public Boolean clearCache(User user) {
        return getRealCache().clearCache(user);
    }

    @Override
    public Boolean refreshTokenExpireTime(String token) {
        long expireTime = config.getUserLoginExpireTime();
        Duration duration = expireTime > 0 ? Duration.ofSeconds(expireTime) : null;
        return getRealCache().refreshTokenExpireTime(token, duration);
    }

    @Override
    public Boolean clearCacheByToken(String token) {
        return getRealCache().clearCacheByToken(token);
    }

    private InnerRedisUserCache getRealCache() {
        if ("1".equals(config.getUserLoginModel())) {
            return userSingleLoginCache;
        } else if ("2".equals(config.getUserLoginModel())) {
            return userMultiLoginCache;
        }
        throw new UnsupportedOperationException(String.format("不支持的登录模式:%S", config.getUserLoginModel()));
    }
}

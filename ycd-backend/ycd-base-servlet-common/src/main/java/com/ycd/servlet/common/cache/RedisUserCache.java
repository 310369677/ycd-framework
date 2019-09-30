package com.ycd.servlet.common.cache;


import com.ycd.common.entity.User;

public interface RedisUserCache {


    Boolean refreshCacheUserInfo(User user);


    String cache(User user);


    User userInfo(String token);

    Boolean clearCache(User user);

    Boolean refreshTokenExpireTime(String token);

    Boolean clearCacheByToken(String token);
}

package com.ycd.servlet.common.cache;



import com.ycd.common.entity.User;

import java.time.Duration;

public interface InnerRedisUserCache {


    String cache(User user);


    User userInfo(String token);

    Boolean clearCache(User user);

    Boolean clearCacheByToken(String token);

    Boolean refreshTokenExpireTime(String token);

    String cache(User user, Duration expire);

    Boolean refreshCacheUserInfo(User user, Duration expire);


    Boolean refreshTokenExpireTime(String token, Duration expire);
}

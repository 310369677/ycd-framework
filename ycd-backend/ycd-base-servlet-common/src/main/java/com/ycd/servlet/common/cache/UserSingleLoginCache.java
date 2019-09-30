package com.ycd.servlet.common.cache;

import com.alibaba.fastjson.JSON;
import com.ycd.common.entity.User;
import com.ycd.common.util.IDUtil;
import com.ycd.common.util.SimpleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

public class UserSingleLoginCache implements InnerRedisUserCache {

    private static final Logger logger = LoggerFactory.getLogger(UserSingleLoginCache.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String cache(User user) {
        return cache(user, null);
    }

    @Override
    public User userInfo(String token) {
        String userStr = stringRedisTemplate.opsForValue().get(token);
        if (SimpleUtil.isEmpty(userStr)) {
            return null;
        }
        return JSON.parseObject(userStr, User.class);
    }

    @Override
    public Boolean clearCache(User user) {
        String key = getKey(user);
        String uuid = stringRedisTemplate.opsForValue().get(key);
        if (SimpleUtil.isNotEmpty(uuid)) {
            stringRedisTemplate.delete(uuid);
        }
        return stringRedisTemplate.delete(key);
    }

    @Override
    public Boolean clearCacheByToken(String token) {
        String userString = stringRedisTemplate.opsForValue().get(token);
        if (SimpleUtil.isEmpty(userString)) {
            return false;
        }
        return clearCache(JSON.parseObject(userString, User.class));
    }

    @Override
    public Boolean refreshTokenExpireTime(String token) {
        return refreshTokenExpireTime(token, null);
    }

    @Override
    public String cache(User user, Duration expire) {

        String key = getKey(user);
        String uuid = IDUtil.uuid();
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (SimpleUtil.isNotEmpty(hasKey) && hasKey) {
            //删除对应的key
            String deleteUuid = stringRedisTemplate.opsForValue().get(key);
            if (SimpleUtil.isNotEmpty(deleteUuid)) {
                stringRedisTemplate.delete(deleteUuid);
            }
            stringRedisTemplate.delete(key);
        }
        //开始保存
        if (SimpleUtil.isNotEmpty(expire)) {
            stringRedisTemplate.opsForValue().set(key, uuid, expire);
            stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(user), expire);
        } else {
            stringRedisTemplate.opsForValue().set(key, uuid);
            stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(user));
        }
        return uuid;
    }

    @Override
    public Boolean refreshCacheUserInfo(User user, Duration expire) {
        logger.debug("刷新用户的缓存根据用户{}", user);
        String key = getKey(user);
        //得到uuid
        String uuid = stringRedisTemplate.opsForValue().get(key);
        if (SimpleUtil.isNotEmpty(uuid)) {
            if (SimpleUtil.isNotEmpty(expire)) {
                stringRedisTemplate.opsForValue().set(key, uuid, expire);
                stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(user), expire);
            } else {
                stringRedisTemplate.opsForValue().set(key, uuid);
                stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(user));
            }
            logger.debug("刷新用户的缓存根据用户成功");
        } else {
            logger.debug("没有找到对应用户的缓存信息{}", user);
            return false;
        }
        return true;
    }

    @Override
    public Boolean refreshTokenExpireTime(String token, Duration expire) {
        String userString = stringRedisTemplate.opsForValue().get(token);
        if (SimpleUtil.isEmpty(userString)) {
            return false;
        }
        User user = JSON.parseObject(userString, User.class);
        return refreshCacheUserInfo(user, expire);
    }


    private String getKey(User user) {
        SimpleUtil.assertNotEmpty(user, "用户不能为空");
        SimpleUtil.assertNotEmpty(user.getId(), "用户id不能为空");
        String prefix = "__single_user_prefix__";
        return prefix + user.getId();
    }
}

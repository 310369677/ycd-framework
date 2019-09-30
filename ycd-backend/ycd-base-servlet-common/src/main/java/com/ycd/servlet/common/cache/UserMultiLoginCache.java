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
import java.util.Random;
import java.util.Set;

public class UserMultiLoginCache implements InnerRedisUserCache {


    private static final Logger log = LoggerFactory.getLogger(UserMultiLoginCache.class);

    private String prefix = "a";
    private String suffix = "a";
    private String sec = "20190529";

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
        if (SimpleUtil.isEmpty(user) || SimpleUtil.isEmpty(user.getId())) {
            return false;
        }
        String pattern = "*" + prefix + sec + user.getId() + suffix + "*";
        Long count = stringRedisTemplate.delete(stringRedisTemplate.keys(pattern));
        return count != null && (count > 0);
    }

    @Override
    public Boolean clearCacheByToken(String token) {
        if (SimpleUtil.isEmpty(token)) {
            return false;
        }
        return stringRedisTemplate.delete(token);
    }

    @Override
    public Boolean refreshTokenExpireTime(String token) {
        return refreshTokenExpireTime(token, null);
    }

    @Override
    public String cache(User user, Duration expire) {
        if (SimpleUtil.isEmpty(user) || SimpleUtil.isEmpty(user.getId())) {
            return null;
        }
        String key = generateKey(user.getId());
        //开始保存key和对应user的信息
        if (SimpleUtil.isNotEmpty(expire)) {
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(user), expire);
            log.debug("缓存用户成功:{}--过期的时间:{}--缓存的key值:{}", user.getNick(), expire, key);
            return key;
        }
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(user));
        log.debug("缓存用户成功:{}--缓存的key值:{}", user.getNick(), key);
        return key;
    }

    @Override
    public Boolean refreshCacheUserInfo(User user, Duration expire) {
        if (SimpleUtil.isEmpty(user) || SimpleUtil.isEmpty(user.getId())) {
            return false;
        }
        String pattern = "*" + prefix + sec + user.getId() + suffix + "*";
        Set<String> set = stringRedisTemplate.keys(pattern);
        if (SimpleUtil.isEmpty(set)) {
            return false;
        }
        return set.stream().map(t -> {
            if (SimpleUtil.isNotEmpty(expire)) {
                stringRedisTemplate.opsForValue().set(t, JSON.toJSONString(user), expire);
                return true;
            }
            stringRedisTemplate.opsForValue().set(t, JSON.toJSONString(user));
            return true;
        }).reduce((t1, t2) -> t1 && t2).get();
    }

    @Override
    public Boolean refreshTokenExpireTime(String token, Duration expire) {
        if (SimpleUtil.isEmpty(token)) {
            return false;
        }
        String userStr = stringRedisTemplate.opsForValue().get(token);
        if (SimpleUtil.isEmpty(userStr)) {
            return false;
        }
        if (SimpleUtil.isNotEmpty(expire)) {
            stringRedisTemplate.opsForValue().set(token, userStr, expire);
            return true;
        }
        stringRedisTemplate.opsForValue().set(token, userStr);
        return true;
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
}

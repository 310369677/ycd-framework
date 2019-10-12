package com.ycd.servlet.security.service.impl;


import com.ycd.common.constant.BooleanEnum;
import com.ycd.common.context.UserContext;
import com.ycd.common.entity.User;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.cache.RedisUserCache;
import com.ycd.servlet.common.service.impl.AbstractServiceWithCreateEntity;
import com.ycd.servlet.security.entity.SecurityUserDetails;
import com.ycd.servlet.security.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractUserServiceImpl<T extends User> extends AbstractServiceWithCreateEntity<T> implements UserService<T> {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RedisUserCache redisUserCache;

    private static final String LOCK_STATUS = "2";

    @Override
    public T findUserByUserName(String userName) {
        SimpleUtil.assertNotEmpty(userName, "用户名不能为空");
        T user = newEntityInstance();
        user.setUserName(userName);
        return mapper.selectOne(user);
    }

    @Override
    public SecurityUserDetails loadUserByUsername(String userName) {
        User user = findUserByUserName(userName);
        SimpleUtil.assertNotEmpty(user, "用户名不存在");
        SimpleUtil.trueAndThrows(LOCK_STATUS.equals(user.getStatus()), "用户已经被锁定");
        return new SecurityUserDetails(user);
    }

    @Override
    @Transactional
    public Long saveUser(T user) {
        SimpleUtil.assertNotEmpty(user.getUserName(), "用户名不能为空");
        //校验用户名
        T instance = newEntityInstance();
        instance.setUserName(user.getUserName());
        SimpleUtil.trueAndThrows(!mapper.select(instance).isEmpty(), "用户名已经存在");
        //预处理用户
        beforeSaveUser(user);
        String password = "123456";
        if (SimpleUtil.isNotEmpty(user.getPassword())) {
            password = user.getPassword();
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(BooleanEnum.NO.code());
        Long id = save(user);
        afterSaveUser(user);
        return id;
    }

    protected void afterSaveUser(T user) {

    }

    protected void beforeSaveUser(T user) {
        //子类重写
    }


    @Override
    @Transactional
    public void updateUser(T user) {
        SimpleUtil.assertNotEmpty(user.getId(), "用户id不能为空");
        if (SimpleUtil.isNotEmpty(user.getUserName())) {
            Example example = new Example(clazz);
            Example.Criteria criteria = example.createCriteria();
            criteria.andNotEqualTo("id", user.getId());
            criteria.andEqualTo("userName", user.getUserName());
            //校验用户名
            SimpleUtil.trueAndThrows(!mapper.selectByExample(example).isEmpty(), "用户名已经存在");
        }
        if (SimpleUtil.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        update(user);
    }

    @Override
    @Transactional
    public void deleteUser(String ids) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        List<Long> idList = Stream.of(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList());
        deleteByIds(idList);
    }

    @Override
    public void changeUserStatus(String ids, String status) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        SimpleUtil.assertNotEmpty(status, "status不能为空");
        Stream.of(ids.split(",")).map(Long::valueOf)
                .forEach(id -> {
                    T instance = newEntityInstance();
                    instance.setStatus(status);
                    //直接覆盖
                    mapper.updateByPrimaryKeySelective(instance);
                    Optional<T> optional = Optional.of(mapper.selectByPrimaryKey(id));
                    if (!optional.isPresent()) {
                        return;
                    }
                    //刷新用户缓存
                    redisUserCache.refreshCacheUserInfo(optional.get());
                });
    }

    @Override
    public void checkCurrentUserPassword(String password) {
        User user = UserContext.getCurrentUser();
        if (SimpleUtil.isEmpty(user)) {
            return;
        }
        String msg = "密码错误";
        T databaseUser = findById(user.getId());
        SimpleUtil.trueAndThrows(!passwordEncoder.matches(password, databaseUser.getPassword()), msg);
    }

    @Override
    public List<T> allUsers() {
        T t = newEntityInstance();
        t.setStatus(BooleanEnum.NO.code());
        return mapper.select(t);
    }
}

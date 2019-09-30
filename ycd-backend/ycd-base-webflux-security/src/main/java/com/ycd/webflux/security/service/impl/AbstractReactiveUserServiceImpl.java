package com.ycd.webflux.security.service.impl;



import com.ycd.common.constant.BooleanEnum;
import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.entity.User;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.cache.ReactiveRedisUserCache;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.impl.AbstractReactiveServiceWithCreateEntity;
import com.ycd.webflux.security.service.interfaces.ReactiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AbstractReactiveUserServiceImpl<T extends User> extends AbstractReactiveServiceWithCreateEntity<T> implements ReactiveUserService<T> {


    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    ReactiveRedisUserCache reactiveRedisUserCache;

    @Override
    public Mono<T> findUserByUserName(String userName) {
        return Mono.fromCallable(() -> {
            SimpleUtil.assertNotEmpty(userName, "用户名不能为空");
            T user = newEntityInstance();
            user.setUserName(userName);
            return mapper.selectOne(user);
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Long> saveUser(T user) {
        user.setStatus(BooleanEnum.NO.code());
        return Mono.defer(() -> beforeSaveUser(user)).map(Void -> user)
                .switchIfEmpty(Mono.just(user)).flatMap(us -> {
                    //校验用户名
                    T instance = newEntityInstance();
                    instance.setUserName(us.getUserName());
                    SimpleUtil.trueAndThrows(!mapper.select(instance).isEmpty(), "用户名已经存在");
                    String password = "123456";
                    if (SimpleUtil.isNotEmpty(us.getPassword())) {
                        password = us.getPassword();
                    }
                    us.setPassword(passwordEncoder.encode(password));
                    return save(us).map(id -> {
                        user.setId(id);
                        return user;
                    });
                }).flatMap(us1 ->
                        Mono.defer(() -> afterSaveUser(us1)
                                .map(Void -> us1)
                                .defaultIfEmpty(us1).map(AbstractEntity::getId)));
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteUser(String ids) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        return Flux.fromArray(ids.split(","))
                .map(id -> {
                    int count = mapper.deleteByPrimaryKey(Long.valueOf(id));
                    return count == 1;
                }).reduce((t1, t2) -> t1 && t2).then();
    }


    protected Mono<Void> afterSaveUser(T user) {
        return Mono.empty();
    }

    protected Mono<Void> beforeSaveUser(T user) {
        return Mono.empty();
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> changeUserStatus(String ids, String status) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        SimpleUtil.assertNotEmpty(status, "status不能为空");
        return Flux.fromArray(ids.split(","))
                .map(id -> {
                    T instance = newEntityInstance();
                    instance.setId(Long.valueOf(id));
                    instance.setStatus(status);
                    //直接覆盖
                    mapper.updateByPrimaryKeySelective(instance);
                    //暂时无视修改结果
                    return id;
                }).flatMap(id -> {
                    User user = mapper.selectByPrimaryKey(Long.valueOf(id));
                    return reactiveRedisUserCache.refreshCacheUserInfo(user).flux();
                }).reduce((t1, t2) -> t1 && t2).then();
    }

}

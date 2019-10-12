package com.ycd.webflux.security.service.interfaces;



import com.ycd.common.entity.User;
import com.ycd.webflux.common.service.interfaces.LongPriReactiveService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface ReactiveUserService<T extends User> extends LongPriReactiveService<T> {

    /**
     * 根据用户名查找用户
     *
     * @param userName 用户名
     * @return null 或者查找到的用户
     */
    Mono<T> findUserByUserName(String userName);

    Mono<UserDetails> findByUsername(String userName);

    /**
     * 保存用户
     *
     * @param user 用户
     * @return 用户的id
     */
    Mono<Long> saveUser(T user);

    /**
     * 删除用户
     *
     * @param ids 删除用户的ids
     * @return 没有返回值
     */
    Mono<Void> deleteUser(String ids);

    /**
     * 改变用户状态
     *
     * @param ids    需要改变的用户ids
     * @param status 需要改变的状态
     * @return 无返回值
     */
    Mono<Void> changeUserStatus(String ids, String status);
}

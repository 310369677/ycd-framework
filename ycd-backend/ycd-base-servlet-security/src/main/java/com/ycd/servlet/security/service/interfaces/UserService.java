package com.ycd.servlet.security.service.interfaces;



import com.ycd.common.entity.User;
import com.ycd.servlet.common.service.interfaces.LongPriService;

import java.util.List;

public interface UserService<T extends User> extends LongPriService<T> {

    /**
     * 根据用户名查找用户
     *
     * @param userName 用户名
     * @return null 或者查找到的用户
     */
    T findUserByUserName(String userName);


    /**
     * 保存用户
     *
     * @param user 用户
     * @return 用户的id
     */
    Long saveUser(T user);


    /**
     * 修改用户
     *
     * @param user 用户
     */
    void updateUser(T user);

    /**
     * 删除用户
     *
     * @param ids 删除用户的ids
     * @return 没有返回值
     */
    void deleteUser(String ids);

    /**
     * 改变用户状态
     *
     * @param ids    需要改变的用户ids
     * @param status 需要改变的状态
     * @return 无返回值
     */
    void changeUserStatus(String ids, String status);


    void checkCurrentUserPassword(String password);

    /**
     * 查询所有的用户
     *
     * @return 用户列表
     */
    List<T> allUsers();

}

package com.ycd.webflux.common.context;


import com.ycd.common.entity.User;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-06-09
 */
public class ReactiveUserContext {

    private User user;

    public ReactiveUserContext(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

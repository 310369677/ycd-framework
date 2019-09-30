package com.ycd.common.util;



import com.ycd.common.context.UserContext;
import com.ycd.common.entity.User;

import java.util.concurrent.Callable;

public class UserContextCallable<V> implements Callable<V> {

    private Callable<V> callable;

    private User user;

    public UserContextCallable(Callable<V> callable, User user) {
        this.callable = callable;
        this.user = user;
    }

    @Override
    public V call() {
        UserContext.setUser(user);
        try {
            return callable.call();
        } catch (Exception e) {
            return null;
        } finally {
            UserContext.remove();
        }
    }
}

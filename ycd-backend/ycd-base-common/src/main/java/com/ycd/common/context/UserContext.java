package com.ycd.common.context;


import com.ycd.common.entity.User;

public class UserContext {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    private UserContext() {
        throw new UnsupportedOperationException("this is not support");
    }

    public static User getCurrentUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static void remove() {
        USER_THREAD_LOCAL.remove();
    }
}

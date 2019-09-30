package com.ycd.common.util;


import com.ycd.common.context.UserContext;
import com.ycd.common.entity.User;

public class UserContextRunnable implements Runnable {

    private Runnable runnable;

    private User user;

    public UserContextRunnable(Runnable runnable, User user) {
        this.runnable = runnable;
        this.user = user;
    }

    @Override
    public void run() {
        UserContext.setUser(user);
        try {
            runnable.run();
        } finally {
            UserContext.remove();
        }
    }
}

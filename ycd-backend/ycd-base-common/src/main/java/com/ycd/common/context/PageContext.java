package com.ycd.common.context;


import com.ycd.common.dto.Page;

public class PageContext {

    private static final ThreadLocal<Page> context = new ThreadLocal<>();

    public static void setPage(Page page) {
        context.set(page);
    }

    public static Page get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}

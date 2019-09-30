package com.ycd.common.util;

import java.util.UUID;

public class IDUtil {


    private IDUtil() {
        throw new UnsupportedOperationException("this is a util");
    }

    private static final SnowflakeIdWorker worker = new SnowflakeIdWorker(0, 0);

    public static long id() {
        return worker.nextId();
    }


    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public static void main(String[] args) {
        System.out.println(IDUtil.id());
    }
}

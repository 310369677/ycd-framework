package com.ycd.common.util;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 同步任务执行器
 */
public class SyncTaskExecutor {

    private Map<String, Callable<?>> callableMap = new ConcurrentHashMap<>();
    private Map<String, Runnable> runnableMap = new ConcurrentHashMap<>();

    /**
     * 提交任务
     *
     * @param name     任务名字
     * @param callable 任务
     */
    public void submit(String name, Callable<?> callable) {
        if (runnableMap.containsKey(name)) {
            return;
        }
        callableMap.putIfAbsent(name, callable);
    }

    /**
     * 提交任务
     *
     * @param name     任务名字
     * @param runnable 任务
     */
    public void submit(String name, Runnable runnable) {
        if (callableMap.containsKey(name)) {
            return;
        }
        runnableMap.putIfAbsent(name, runnable);
    }

    public Map<String, Object> start(String... names) {
        return start(Arrays.asList(names));
    }

    public Map<String, Object> startAll() {
        Set<String> set = new HashSet<>();
        set.addAll(runnableMap.keySet());
        set.addAll(callableMap.keySet());
        return start(set);
    }

    public Map<String, Object> start(Collection<String> nameList) {
        int count = 0;
        //计算有效的数量
        for (String name : nameList) {
            if (runnableMap.containsKey(name) || callableMap.containsKey(name)) {
                count++;
            }
        }
        if (count == 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<>();
        //开始任务
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (String name : nameList) {
            if ((!runnableMap.containsKey(name)) && (!callableMap.containsKey(name))) {
                continue;
            }
            Runnable runnable = runnableMap.remove(name);
            if (SimpleUtil.isNotEmpty(runnable)) {
                GlobalThreadPool.submit(() -> {
                    runnable.run();
                    countDownLatch.countDown();
                });
            }
            Callable callable = callableMap.remove(name);
            if (SimpleUtil.isNotEmpty(callable)) {
                GlobalThreadPool.submit(() -> {
                    try {
                        Object res = callable.call();
                        result.putIfAbsent(name,res);
                        countDownLatch.countDown();
                        return res;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            }
        }
        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
            return result;
        } catch (InterruptedException e) {
            return Collections.emptyMap();
        }
    }


}

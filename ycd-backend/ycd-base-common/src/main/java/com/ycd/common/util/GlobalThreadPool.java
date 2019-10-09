package com.ycd.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class GlobalThreadPool {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Logger logger = LoggerFactory.getLogger(GlobalThreadPool.class);

    private GlobalThreadPool() {
        throw new RuntimeException("this is pool util");
    }

    public static Future<?> submit(Runnable task) {
        return EXECUTOR_SERVICE.submit(new SafeRunnableProxy(task));
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return EXECUTOR_SERVICE.submit(new SafeCallableProxy(task));
    }


    public static Future<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return SCHEDULED_EXECUTOR_SERVICE.schedule(new SafeRunnableProxy(task), delay, unit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                            long initialDelay,
                                                            long delay,
                                                            TimeUnit unit) {
        return SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(new SafeRunnableProxy(command), initialDelay, delay, unit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                         long initialDelay,
                                                         long period,
                                                         TimeUnit unit) {
        return SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new SafeRunnableProxy(command), initialDelay, period, unit);
    }


    private static class SafeRunnableProxy implements Runnable {

        private Runnable runnable;


        public SafeRunnableProxy(Runnable runnable) {
            SimpleUtil.assertNotEmpty(runnable, "任务不能为空");
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
                logger.debug("任务执行成功{}", runnable);
            } catch (Throwable e) {
                //任务失败了不管
                logger.warn("任务执行失败", e);
            }
        }
    }

    private static class SafeCallableProxy implements Callable {

        private Callable<?> callable;


        public SafeCallableProxy(Callable<?> callable) {
            SimpleUtil.assertNotEmpty(callable, "任务不能为空");
            this.callable = callable;
        }


        @Override
        public Object call() throws Exception {
            try {
                Object result = callable.call();
                logger.debug("任务执行成功{}", callable);
                return result;
            } catch (Throwable e) {
                //任务失败了不管
                logger.warn("任务执行失败", e);
                return null;
            }
        }
    }

}

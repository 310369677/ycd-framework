package com.ycd.common.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Aspect
public class ExecuteTimeAspect {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTimeAspect.class);


    /*
     * 定义一个切入点
     */
    @Pointcut("@within(com.ycd.common.anno.TimeCalculate)")
    public void pointCut() {
    }

    @Pointcut("@annotation(com.ycd.common.anno.TimeCalculate)")
    public void methodPointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return calculateTime(point);
    }

    @Around("methodPointCut()")
    public Object methodPoint(ProceedingJoinPoint point) throws Throwable {
        return calculateTime(point);
    }

    private Object calculateTime(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        long start = System.nanoTime();
        Object returnVal = point.proceed();
        log.debug("当前执行方法:{},耗时:{}ms", signature.toLongString(), Duration.ofNanos(System.nanoTime() - start).toMillis());
        return returnVal;
    }
}

package com.ycd.webflux.common.aop;


import com.ycd.common.exception.BusinessException;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.config.WebFluxBeanManager;
import com.ycd.webflux.common.reactivetransaction.ReactiveTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

/**
 * 事务支持切面
 */
@Configuration
@Aspect
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxTransactionalSupportAop {


    private static final Logger log = LoggerFactory.getLogger(WebFluxTransactionalSupportAop.class);


    @Autowired
    ReactiveTransaction reactiveTransaction;

    /*
     * 定义一个切入点
     */
    // @Pointcut("execution (* findById*(..))")
    @Pointcut("@annotation(com.ycd.webflux.common.reactivetransaction.WebFluxTransactional)")
    public void methodPointCut() {
    }

    @Pointcut("@within(com.ycd.webflux.common.reactivetransaction.WebFluxTransactional)")
    public void withInPointCut() {

    }


    @Around("methodPointCut()")
    public Object around(ProceedingJoinPoint point) {
        return doProcessInTransaction("methodPointCut", point);
    }

    @Around("withInPointCut()")
    public Object around1(ProceedingJoinPoint point) {
        return doProcessInTransaction("withInPointCut", point);
    }

    private Object doProcessInTransaction(String name, ProceedingJoinPoint point) {
        log.debug("切面{}开始执行{}", name, point);
        Signature signature = point.getSignature();
        Class<?> clazz = signature.getDeclaringType();
        Method[] methods = clazz.getMethods();
        Method targetMethod = findMethod(signature, methods);
        SimpleUtil.assertNotEmpty(targetMethod, "方法没找到");
        Class<?> returnType = targetMethod.getReturnType();
        if (Publisher.class.isAssignableFrom(targetMethod.getReturnType())) {
            Object obj = doProcess(point, returnType);
            log.debug("切面{}结束", name);
            return obj;
        }
        try {
            return point.proceed();
        } catch (Throwable e) {
            throw convert2RuntimeException(e);
        }
    }


    private Object doProcess(ProceedingJoinPoint point, Class<?> returnType) {
        if (returnType == Mono.class) {
            return reactiveTransaction.executeAndReturnMono(status -> {
                try {
                    return (Mono<?>) point.proceed();
                } catch (Throwable throwable) {
                    throw convert2RuntimeException(throwable);
                }
            });
        } else if (returnType == Flux.class) {
            return reactiveTransaction.executeAndReturnFlux(status -> {
                try {
                    return (Flux<?>) point.proceed();
                } catch (Throwable throwable) {
                    throw convert2RuntimeException(throwable);
                }
            });
        }
        throw new BusinessException("not support publiser");
    }

    private Method findMethod(Signature signature, Method[] methods) {
        //查找方法
        for (Method method : methods) {
            String methodFullName = method.toString();
            if ((!methodFullName.equals(signature.toLongString()))) {
                continue;
            }
            return method;
        }
        return null;
    }

    private RuntimeException convert2RuntimeException(Throwable e) {
        if (e instanceof BusinessException) {
            return (BusinessException) e;
        }
        return new RuntimeException(e);
    }


}

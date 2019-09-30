package com.ycd.webflux.common.reactivetransaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public class ReactiveTransaction {


    private ReactiveTransactionTemplate transactionTemplete;


    public ReactiveTransaction(PlatformTransactionManager transactionManager) {
        this.transactionTemplete = new ReactiveTransactionTemplate(transactionManager);
    }

    public <T> Mono<T> executeAndReturnMono(@NonNull MonoTransactionCallback<T> action) {
        return transactionTemplete.executeAndReturnMono(action);
    }


    public <T> Flux<T> executeAndReturnFlux(FluxTransactionCallback<T> action) {
        return transactionTemplete.executeAndReturnFlux(action);
    }

    public Object doBlockedProcess(ProceedingJoinPoint point) {
        return transactionTemplete.doBlockedProcess(point);
    }
}

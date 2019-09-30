package com.ycd.webflux.common.reactivetransaction;

import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveTransactionOperations {
    @NonNull
    <T> Mono<T> executeAndReturnMono(MonoTransactionCallback<T> action);

    <T> Flux<T> executeAndReturnFlux(FluxTransactionCallback<T> action);
}

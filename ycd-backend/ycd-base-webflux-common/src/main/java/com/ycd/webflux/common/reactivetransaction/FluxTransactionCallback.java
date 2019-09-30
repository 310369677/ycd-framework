package com.ycd.webflux.common.reactivetransaction;

import org.springframework.transaction.TransactionStatus;
import reactor.core.publisher.Flux;

public interface FluxTransactionCallback<T> {
    Flux<T> doInTransactionAndReturnFlux(TransactionStatus status);
}

package com.ycd.webflux.common.reactivetransaction;

import org.springframework.transaction.TransactionStatus;
import reactor.core.publisher.Mono;

public interface MonoTransactionCallback<T> {
    Mono<T> doInTransactionAndReturnMono(TransactionStatus status);
}

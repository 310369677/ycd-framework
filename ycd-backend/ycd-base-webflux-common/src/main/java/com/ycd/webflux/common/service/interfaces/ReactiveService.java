package com.ycd.webflux.common.service.interfaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveService<ID, T> {

    Mono<ID> save(T t);

    Flux<ID> saveList(List<T> list);

    Mono<T> update(T t);

    Flux<T> updateList(List<T> list);

    Mono<Void> deleteById(ID id);

    Mono<Void> deleteByIds(List<ID> ids);

    Mono<T> findById(ID id);
}

package com.ycd.webflux.common.service.interfaces;

import com.ycd.common.entity.Dic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveDicService extends LongPriReactiveService<Dic> {

    Mono<Long> saveDic(Dic dic);

    Mono<Void> updateDic(Dic dic);

    Mono<Void> deleteDicByIds(String ids);

    Flux<Dic> queryDicByName(String name);
}

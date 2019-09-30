package com.ycd.webflux.common.context;


import com.ycd.common.dto.Page;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * page相关上下文
 */
public class ReactivePageHolder {


    private static final String PAGE_KEY = Page.class.getName();

    public static Mono<Page> pageMono() {
        return Mono.subscriberContext()
                .filter(c -> c.hasKey(PAGE_KEY))
                .flatMap(c -> c.get(PAGE_KEY));
    }

    public static Context withPageMono(Mono<Page> pageMono) {
        return Context.of(PAGE_KEY, pageMono);
    }


}

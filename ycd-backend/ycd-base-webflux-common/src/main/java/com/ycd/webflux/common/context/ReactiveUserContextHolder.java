package com.ycd.webflux.common.context;


import com.ycd.common.entity.User;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-06-09
 */
public class ReactiveUserContextHolder {

    public static final Class<?> USER_CONTEXT_KEY = ReactiveUserContext.class;

    public static Mono<ReactiveUserContext> userContext() {
        return Mono.subscriberContext()
                .filter(c -> c.hasKey(USER_CONTEXT_KEY))
                .flatMap(c -> c.<Mono<ReactiveUserContext>>get(USER_CONTEXT_KEY));
    }


    public static Context withUserContext(Mono<? extends ReactiveUserContext> userContext) {
        return Context.of(USER_CONTEXT_KEY, userContext);
    }


    public static Context withUser(User user) {
        return withUserContext(Mono.just(new ReactiveUserContext(user)));
    }

    public static Context withUserMono(Mono<User> mono) {
        return withUserContext(mono.flatMap(user -> Mono.just(new ReactiveUserContext(user))));
    }


}

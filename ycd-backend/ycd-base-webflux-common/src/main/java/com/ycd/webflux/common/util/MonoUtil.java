package com.ycd.webflux.common.util;

import com.ycd.common.util.SimpleUtil;
import reactor.core.publisher.Mono;

public class MonoUtil {

    /**
     * 业务异常
     *
     * @param message 消息
     * @return 结果
     */
    public static <T> Mono<T> businessError(String message) {
        return Mono.error(SimpleUtil.newBusinessException(message));
    }
}

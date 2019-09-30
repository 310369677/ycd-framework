package com.ycd.webflux.security.security;


import com.ycd.common.Result;
import com.ycd.common.constant.StatusCode;
import com.ycd.webflux.common.util.ResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class AuthAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        Result result = convertException(denied);
        return ResponseUtil.writeEntityInfo(exchange.getResponse(), result);
    }

    private Result convertException(AccessDeniedException denied) {
        Result result = Result.error("未转换异常");
        if (denied instanceof AuthorizationServiceException) {
            result = Result.result(StatusCode.NO_PRIVILEGE);
        }
        return result;
    }
}

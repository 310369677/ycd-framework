package com.ycd.webflux.security.security;


import com.ycd.common.Result;
import com.ycd.common.constant.StatusCode;
import com.ycd.webflux.common.util.ResponseUtil;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class AuthEnteryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        Result result = convertException(e);
        ServerHttpResponse response = exchange.getResponse();
        return ResponseUtil.writeEntityInfo(response, result);
    }

    private Result convertException(AuthenticationException e) {
        Result result = Result.error("未转换异常");
        if (e instanceof AuthenticationCredentialsNotFoundException) {
            result = Result.result(StatusCode.NO_AUTHTICATION);
        }
        return result;
    }
}

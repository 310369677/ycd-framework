package com.ycd.webflux.security.security;

import com.ycd.common.Result;
import com.ycd.webflux.common.util.ResponseUtil;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 处理登录验证失败的类
 */


public class AuthFailureHandler implements ServerAuthenticationFailureHandler {


    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        Result result = convertException2Result(exception);
        ServerHttpResponse response = exchange.getResponse();
        return ResponseUtil.writeEntityInfo(response, result);
    }

    private Result convertException2Result(AuthenticationException e) {
        Result result = Result.error("授权未捕获异常");
        if (e instanceof UsernameNotFoundException) {
            result = Result.error("用户名或密码错误");
        } else if (e instanceof AuthenticationCredentialsNotFoundException) {
            result = Result.error("密码不能为空");
        } else if (e instanceof BadCredentialsException) {
            result = Result.error("用户名或密码错误");
        }
        return result;
    }
}

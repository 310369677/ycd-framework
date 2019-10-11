package com.ycd.webflux.security.security;

import com.ycd.common.Result;
import com.ycd.common.exception.BusinessException;
import com.ycd.webflux.common.cache.ReactiveRedisUserCache;
import com.ycd.webflux.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理授权成功的处理器
 */


public class AuthSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private ReactiveRedisUserCache reactiveRedisUserCache;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) authentication.getPrincipal();
        Mono<String> tokenM = reactiveRedisUserCache.cache(securityUserDetails);
        return tokenM
                .switchIfEmpty(Mono.error(new BusinessException("生成token失败")))
                .flatMap(token -> {
                            Map<String, Object> result = new HashMap<>();
                            result.putIfAbsent("token", token);
                            securityUserDetails.setPassword(null);
                            result.putIfAbsent("user", securityUserDetails);
                            return ResponseUtil.writeEntityInfo(response, Result.ok(result));
                        }
                )
                .onErrorResume(e -> ResponseUtil.writeException(response, e));
    }
}

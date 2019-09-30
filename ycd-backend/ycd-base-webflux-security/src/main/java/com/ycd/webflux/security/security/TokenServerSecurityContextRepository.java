package com.ycd.webflux.security.security;


import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.entity.User;
import com.ycd.common.exception.BusinessException;
import com.ycd.webflux.common.context.ReactiveUserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class TokenServerSecurityContextRepository implements ServerSecurityContextRepository {


    private static final Logger log = LoggerFactory.getLogger(TokenServerSecurityContextRepository.class);


    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    /**
     * 该方法表示产生上下文,产生上下文从token里面获取
     *
     * @param exchange
     * @return
     */
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return ReactiveUserContextHolder.userContext().flatMap(userContext -> getAuthentication(Mono.just(userContext.getUser())))
                .flatMap(authenticationToken -> {
                    SecurityContext securityContext = new SecurityContextImpl();
                    securityContext.setAuthentication(authenticationToken);
                    return Mono.just(securityContext);
                });
    }


    private Mono<UsernamePasswordAuthenticationToken> getAuthentication(Mono<AbstractEntity> userMono) {
        return userMono.switchIfEmpty(Mono.error(new BusinessException("无效token"))).flatMap(user -> {
            SecurityUserDetails userDetails = new SecurityUserDetails((User) user);
            //此处password不能为null
            org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(userDetails.getUsername(), "", userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, userDetails.getAuthorities());
            return Mono.just(usernamePasswordAuthenticationToken);
        });
    }

    public static void main(String[] args) {

    }

}

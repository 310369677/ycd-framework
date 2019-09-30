package com.ycd.webflux.security.security;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.config.security.SecurityConfig;
import com.ycd.webflux.common.cache.ReactiveRedisUserCache;
import com.ycd.webflux.common.config.WebFluxBeanManager;
import com.ycd.webflux.security.config.ReactiveSecurityBeanConfig;
import com.ycd.webflux.security.filter.ReactorUserInfoWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({CommonConfig.class,SecurityConfig.class})
public class WebFluxSecurityConfig {


    @Autowired
    ReactiveRedisUserCache reactiveRedisUserCache;


    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private CommonConfig commonConfig;


    /**
     * 获取securityContext上下文工程
     */
    @Autowired
    ServerSecurityContextRepository tokenServerSecurityContextRepository;


    /**
     * 鉴权成功处理器
     */
    @Autowired
    ServerAuthenticationSuccessHandler authSuccessHandler;

    /**
     * 鉴权失败处理器
     */
    @Autowired
    ServerAuthenticationFailureHandler authFailureHandler;

    /**
     * 处理未认证
     */
    @Autowired
    ServerAuthenticationEntryPoint authEnteryPoint;


    /**
     * 处理权限不足
     */
    @Autowired
    ServerAccessDeniedHandler authAccessDeniedHandler;

    /**
     * 处理登出
     */
    @Autowired
    ServerLogoutSuccessHandler customLogoutSuccessHandler;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1");
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http.exceptionHandling()
                .authenticationEntryPoint(authEnteryPoint)
                .accessDeniedHandler(authAccessDeniedHandler).and()
                .authorizeExchange()
                //.pathMatchers("/**")
                .pathMatchers(securityConfig.getAnnoUrls().split(","))
                .permitAll()
                .anyExchange()
                .authenticated()
                /*.pathMatchers("/swagger-*")
                .permitAll()*/
                .and()
                .csrf().disable()
                .httpBasic()
                //.csrfTokenRepository(customCsrfTokenRepository)
                //.requireCsrfProtectionMatcher(customCsrfMatcher)
                .and()
                .formLogin()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationEntryPoint(authEnteryPoint)
                //认证失败处理器
                .authenticationFailureHandler(authFailureHandler)
                .authenticationSuccessHandler(authSuccessHandler)
                .requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, securityConfig.getLoginUrl()))
                //.loginPage("/login")
                //.authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/login?error"))
                //.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/admin"))
                .and()
                .logout()
                .logoutUrl(securityConfig.getLogOutUrl())
                .logoutSuccessHandler(customLogoutSuccessHandler);
        ReactorUserInfoWebFilter reactorUserInfoWebFilter = new ReactorUserInfoWebFilter();
        reactorUserInfoWebFilter.setCommonConfig(commonConfig);
        reactorUserInfoWebFilter.setReactiveRedisUserCache(reactiveRedisUserCache);
        http.addFilterAt(reactorUserInfoWebFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        http.securityContextRepository(tokenServerSecurityContextRepository);
        return http.build();
    }

    public static void main(String[] args) {
       /* PasswordEncoder passwordEncoder=new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1");
        System.out.println(passwordEncoder.encode("111111"));*/
        System.out.println(System.currentTimeMillis());
    }


}

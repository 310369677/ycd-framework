package com.ycd.servlet.security.config;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.config.security.SecurityConfig;
import com.ycd.servlet.common.cache.RedisUserCache;
import com.ycd.servlet.common.filter.UserInfoFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

@EnableWebSecurity
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({CommonConfig.class, SecurityConfig.class})
public class ServletWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


    @Autowired
    RedisUserCache redisUserCache;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;


    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        HttpSecurity security = http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and().authorizeRequests()
                .antMatchers(securityConfig.getAnnoUrls().split(",")).permitAll().and();

        //验证码打开的话，这个请求直接可以匿名
        if (securityConfig.isOpenCode()) {
            security.authorizeRequests().antMatchers("/code/make").permitAll();
        }

        security.authorizeRequests().anyRequest().authenticated().and()
                .formLogin()
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
                .loginProcessingUrl(securityConfig.getLoginUrl())
                .and()
                .logout()
                .logoutUrl(securityConfig.getLogOutUrl())
                .logoutSuccessHandler(logoutSuccessHandler);
        UserInfoFilter userInfoFilter = new UserInfoFilter();
        userInfoFilter.setConfig(commonConfig);
        userInfoFilter.setRedisUserCache(redisUserCache);
        http.addFilterBefore(userInfoFilter, SecurityContextPersistenceFilter.class);
        http.securityContext().securityContextRepository(securityContextRepository);
    }


}

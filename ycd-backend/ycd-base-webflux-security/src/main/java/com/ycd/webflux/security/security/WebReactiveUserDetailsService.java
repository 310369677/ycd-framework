package com.ycd.webflux.security.security;


import com.ycd.common.entity.User;
import com.ycd.webflux.security.service.interfaces.ReactiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

public class WebReactiveUserDetailsService implements ReactiveUserDetailsService {


    @Autowired
    private ReactiveUserService<User> reactiveUserService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return reactiveUserService.findByUsername(username);
    }
}

package com.yang.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("test")
    public Mono<String> test(){
        return Mono.just("success");
    }
}

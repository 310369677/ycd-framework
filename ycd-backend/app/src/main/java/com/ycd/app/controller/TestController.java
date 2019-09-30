package com.ycd.app.controller;

import com.ycd.common.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping("test")
    public Result<String> test() {
        return Result.ok();
    }
}

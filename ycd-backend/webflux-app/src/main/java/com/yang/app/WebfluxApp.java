package com.yang.app;

import com.ycd.common.YcdApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan(basePackages = {
        "com.ycd.common.repo.mybatis"
})
public class WebfluxApp extends YcdApplication {

    public static void main(String[] args) {
        YcdApplication.of(WebfluxApp.class).web(WebApplicationType.REACTIVE).run(args);
    }
}

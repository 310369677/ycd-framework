package com.yang.app;

import com.ycd.common.YcdApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class WebfluxApp extends YcdApplication {

    public static void main(String[] args) {
        YcdApplication.of(WebfluxApp.class).web(WebApplicationType.REACTIVE).run(args);
    }
}

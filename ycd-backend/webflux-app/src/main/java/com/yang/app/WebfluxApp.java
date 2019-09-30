package com.yang.app;

import com.ycd.common.YcdApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = "com.ycd")
public class WebfluxApp extends YcdApplication {

    public static void main(String[] args) {
        YcdApplication.of(WebfluxApp.class,"1.0").web(WebApplicationType.REACTIVE).run(args);
    }
}

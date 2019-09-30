package com.ycd.app;


import com.ycd.common.YcdApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(scanBasePackages = "com.ycd")
public class App extends YcdApplication {

    public static void main(String[] args) {
        YcdApplication.of(App.class,"1.0").web(WebApplicationType.SERVLET).run(args);
        //new SpringApplicationBuilder(App.class).web(WebApplicationType.SERVLET).run(args);
    }
}

package com.ycd.app;


import com.ycd.common.YcdApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


public class App extends YcdApplication {

    public static void main(String[] args) {
        YcdApplication.of(App.class).web(WebApplicationType.SERVLET).run(args);
        //new SpringApplicationBuilder(App.class).web(WebApplicationType.SERVLET).run(args);
    }
}

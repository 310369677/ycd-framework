package com.ycd.app;


import com.ycd.common.YcdApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {
        "com.ycd.common.repo.mybatis"
})
public class App extends YcdApplication {

    public static void main(String[] args) {
        YcdApplication.of(App.class).web(WebApplicationType.SERVLET).run(args);
        //new SpringApplicationBuilder(App.class).web(WebApplicationType.SERVLET).run(args);
    }
}

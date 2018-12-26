package com.greentown.lottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class LotteryApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run =
                SpringApplication.run(LotteryApplication.class, args);
    }

}


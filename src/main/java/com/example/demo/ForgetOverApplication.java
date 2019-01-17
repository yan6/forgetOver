package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan(basePackages = "com.example.demo.dao.mapper")
@ImportResource("classpath:/redis/application-redis.xml")
public class ForgetOverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForgetOverApplication.class, args);
    }
}

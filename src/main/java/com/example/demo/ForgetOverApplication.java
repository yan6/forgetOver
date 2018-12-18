package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.example.demo.dao.mapper")
public class ForgetOverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForgetOverApplication.class, args);
    }
}

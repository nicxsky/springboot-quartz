package com.example.quartz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.quartz.dao")
@SpringBootApplication
public class QuartzJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzJobApplication.class, args);
    }

}

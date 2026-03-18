package com.yeahicode.ucbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yeahicode.ucbackend.mapper")
public class UcbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UcbackendApplication.class, args);
    }

}

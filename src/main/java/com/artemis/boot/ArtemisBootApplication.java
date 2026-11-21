package com.artemis.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.artemis.boot.mapper")
public class ArtemisBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtemisBootApplication.class, args);
    }

}

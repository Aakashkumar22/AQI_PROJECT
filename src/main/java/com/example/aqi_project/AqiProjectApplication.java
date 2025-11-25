package com.example.aqi_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
// Add this if using AspectJ solution:

public class AqiProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AqiProjectApplication.class, args);
    }
}


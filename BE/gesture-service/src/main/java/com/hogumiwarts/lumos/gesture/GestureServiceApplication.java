package com.hogumiwarts.lumos.gesture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class, scanBasePackages = "com.hogumiwarts.lumos")
@EnableFeignClients(basePackages = "com.hogumiwarts.lumos.gesture.client")
@EnableScheduling
public class GestureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestureServiceApplication.class, args);
    }

}

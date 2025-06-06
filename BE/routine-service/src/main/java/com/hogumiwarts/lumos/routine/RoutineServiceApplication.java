package com.hogumiwarts.lumos.routine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class, scanBasePackages = "com.hogumiwarts.lumos")
@EnableFeignClients(basePackages = "com.hogumiwarts.lumos.routine.client")
public class RoutineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutineServiceApplication.class, args);
	}

}

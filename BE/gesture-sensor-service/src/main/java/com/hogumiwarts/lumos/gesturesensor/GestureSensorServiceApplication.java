package com.hogumiwarts.lumos.gesturesensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class, scanBasePackages = "com.hogumiwarts.lumos")
@EnableScheduling
public class GestureSensorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestureSensorServiceApplication.class, args);
	}

}

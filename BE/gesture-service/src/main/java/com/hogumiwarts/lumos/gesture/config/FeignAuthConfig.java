package com.hogumiwarts.lumos.gesture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignAuthConfig {
	@Bean
	public RequestInterceptor requestInterceptor() {
		return template -> {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attrs != null) {
				HttpServletRequest request = attrs.getRequest();
				String token = request.getHeader("Authorization");
				if (token != null) {
					template.header("Authorization", token);
				}
			}
		};
	}
}

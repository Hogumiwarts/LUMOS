package com.hogumiwarts.lumos.routine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	private Info apiInfo() {
		return new Info()
			.title("[LUMOS] REST API")
			.description("SSAFY 자율 프로젝트 **LUMOS 서비스의 API 명세서**입니다.")
			.version("v1.0")
			.contact(new Contact().name("Team Hogumiwarts")
				.email("www.hogumiwarts.com")
				.url("suhmiji@gmail.com"))
			.license(new License()
				.name("License of API")
				.url("API license URL"));
	}

	private SecurityScheme createApiKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	@Bean
	@Profile("prod")  // 운영 환경
	public OpenAPI gatewayApi() {
		return new OpenAPI()
			.addServersItem(new Server().url("/routine"))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components().addSecuritySchemes("Bearer Authentication", createApiKeyScheme()))
			.info(apiInfo());
	}

	@Bean
	@Profile("dev")  // 로컬 개발 환경
	public OpenAPI localApi() {
		return new OpenAPI()
			.addServersItem(new Server().url("D"))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components().addSecuritySchemes("Bearer Authentication", createApiKeyScheme()))
			.info(apiInfo());
	}
}

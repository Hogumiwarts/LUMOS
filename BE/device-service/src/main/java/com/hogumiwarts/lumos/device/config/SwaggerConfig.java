package com.hogumiwarts.lumos.device.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	private Info apiInfo() {
		return new Info()
			.title("[LUMOS] REST API")
			.description("SSAFY 자율 프로젝트 **LUMOS 서비스의 API 명세서**입니다.")
			.version("v1.0")
			.contact(new Contact()
				.name("Team Hogumiwarts")
				.email("suhmiji@gmail.com")
				.url("www.hogumiwarts.com"))
			.license(new License()
				.name("License of API")
				.url("API license URL"));
	}

	private SecurityScheme createApiKeyScheme() {
		return new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.addServersItem(new Server().url("/device"))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components().addSecuritySchemes("Bearer Authentication", createApiKeyScheme()))
			.info(apiInfo())
			.addTagsItem(new Tag()
				.name("기기정보 조회")
				.description("회원이 등록한 디바이스 정보를 조회하는 API입니다."))
			.addTagsItem(new Tag()
				.name("스위치")
				.description("스위치 관련 상태 및 제어 API입니다."))
			.addTagsItem(new Tag()
				.name("스피커")
				.description("스피커 관련 상태 및 제어 API입니다."))
			.addTagsItem(new Tag()
				.name("조명")
				.description("조명 관련 상태 및 제어 API입니다."));
	}
}

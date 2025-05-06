package com.hogumiwarts.lumos.device.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;




@Configuration
public class WebClientConfig {

    /**
     * WebClient 설정
     * - baseUrl: Node.js 기반 디바이스 제어 서비스 주소
     * - 'node-service'는 docker-compose 또는 MSA 환경에서 설정된 서비스 이름 예시
     * - 실제 요청 경로는 WebClient 호출부에서 .uri(...)를 통해 붙음
     *
     * 예: webClient.get().uri("/api/devices/{id}/status")
     *  -> 실제 호출: http://node-service/api/devices/1/status
     *
     * 주의: Node.js API 경로 변경 시 이 baseUrl 및 호출부 점검 필요
     */
    @Value("${node.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}

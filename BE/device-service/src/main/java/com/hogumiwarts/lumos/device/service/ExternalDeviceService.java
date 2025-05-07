package com.hogumiwarts.lumos.device.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalDeviceService {

    private final DeviceRepository deviceRepository;
    private final WebClient webClient;

    // SmartThings API : 등록된 디바이스 목록 조회
    public DeviceListResponse fetchDeviceList(String installedAppId) {
        return webClient.get()
                .uri("/devices")
                .header("installedappid", installedAppId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API 오류 응답: " + body))
                )
                .bodyToMono(DeviceListResponse.class)
                .block(Duration.ofSeconds(5));  // timeout 걸기!
    }

    // 디바이스 제어 명령어 조회 API
    public JsonNode fetchDeviceCommands(String deviceId, String capabilityId, String installedAppId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/devices/{deviceId}/components/main/capabilities/{capabilityId}/commands")
                        .build(deviceId, capabilityId))
                .header("installedappid", installedAppId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API 오류 응답: " + body))
                )
                .bodyToMono(JsonNode.class)
                .block(Duration.ofSeconds(5));  // timeout 걸기!
    }


    // 디바이스 상태 조회
    public JsonNode fetchDeviceStatus(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기기를 찾을 수 없습니다."));

        String controlDeviceId = device.getControlId();
        String installedAppId = device.getInstalledAppId();

        // 이걸 JSON Body 또는 Header에 넣어야 함
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/devices/{controlDeviceId}/status")
                        .build(controlDeviceId))
                .header("installedappid", installedAppId) // header 요구 시
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("API 오류 응답: " + body))
                )
                .bodyToMono(JsonNode.class)
                .block(Duration.ofSeconds(5));  // timeout 걸기!
    }

    // 전원, 볼륨 등 제어 명령어
    public <T> T executeCommand(Long deviceId, Object body, Class<T> responseType) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("기기를 찾을 수 없습니다."));

        String controlDeviceId = String.valueOf(device.getControlId());
        String installedAppId = device.getInstalledAppId();


        // 로그 확인용
        log.debug("----- Log: executeCommand ------ ", "");
        log.debug("-", "[ Node.js API 요청 ]");
        log.debug("-", "- 요청 URL : /devices/" + controlDeviceId + "/command");
        log.debug("-", "- Header(installedappid) : " + installedAppId);
        log.debug("-", "- 내부 body : " + body.toString());
        log.debug("----- Log: executeCommand ------ ", "");

        return webClient.post()
                .uri("/devices/{controlDeviceId}/command", controlDeviceId)
                .header("installedappid", installedAppId)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException("API 오류 응답: " + errorBody))
                )
                .bodyToMono(responseType)
                .block(Duration.ofSeconds(5));  // timeout 걸기!
    }

}
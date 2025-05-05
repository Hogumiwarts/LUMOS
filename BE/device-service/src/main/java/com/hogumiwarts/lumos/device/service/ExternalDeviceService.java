package com.hogumiwarts.lumos.device.service;


import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalDeviceService {

    private final DeviceRepository deviceRepository;
    private final WebClient webClient;

    // SmartThings API : 등록된 디바이스 목록 조회
    public DeviceListResponse fetchDeviceList(String installedAppId) {
        System.out.println("[디바이스 목록 조회 요청]");
        System.out.println("- 요청 URL : /smart/devices");
        System.out.println("- Header(installedappid) : " + installedAppId);

        return webClient.get()
                .uri("/smart/devices")
                .header("installedappid", installedAppId)
                .retrieve()
                .bodyToMono(DeviceListResponse.class)
                .block();
    }


    // 디바이스 상태 조회
    public DeviceStatusResponse fetchDeviceStatus(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기기를 찾을 수 없습니다."));

        // Node.js가 요구하는 파라미터 추출
        // Test : 미니빅 스위치
        String controlDeviceId = "90884518-cfe8-4d94-a476-338682820822";
        String installedAppId = "11237955-08ec-418f-a2cd-8c7ca1a0919e";
//        String controlDeviceId = String.valueOf(device.getControlId());
//        String installedAppId = device.getInstalledAppId();


        // 로그 확인용
        System.out.println("[디바이스 상태 조회 요청]");
        System.out.println("- 요청 URL : /smart/devices/" + controlDeviceId + "/status");
        System.out.println("- Header(installedappid) : " + installedAppId);
        System.out.println("- 내부 deviceId : " + deviceId);

        // 이걸 JSON Body 또는 Header에 넣어야 함
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/smart/devices/{controlDeviceId}/status")
                        .build(controlDeviceId))
                .header("installedappid", installedAppId) // header 요구 시
                .retrieve()
                .bodyToMono(DeviceStatusResponse.class)
                .block();
    }

    // 전원, 볼륨 제어 명령어
    public <T> T executeCommand(Long deviceId, Object body, Class<T> responseType) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("기기를 찾을 수 없습니다."));

        // Node.js가 요구하는 파라미터 추출
        // Test : 미니빅 스위치
        String controlDeviceId = "90884518-cfe8-4d94-a476-338682820822";
        String installedAppId = "11237955-08ec-418f-a2cd-8c7ca1a0919e";
//        String controlDeviceId = String.valueOf(device.getControlId());
//        String installedAppId = device.getInstalledAppId();


        // 로그 확인용
        System.out.println("[디바이스 상태 조회 요청]");
        System.out.println("- 요청 URL : /smart/devices/" + controlDeviceId + "/status");
        System.out.println("- Header(installedappid) : " + installedAppId);
        System.out.println("- 내부 deviceId : " + deviceId);

        return webClient.post()
                .uri("/smart/devices/{controlDeviceId}/command", controlDeviceId)
                .header("installedappid", installedAppId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

}
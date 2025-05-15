package com.hogumiwarts.lumos.device.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogumiwarts.lumos.device.client.SmartThingsClient;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.device.DeviceListResponse;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalDeviceService {

    private final DeviceRepository deviceRepository;
    private final SmartThingsClient smartThingsClient;
    private final ObjectMapper objectMapper; // 👉 추가!

    // SmartThings API : 등록된 디바이스 목록 조회
    public DeviceListResponse fetchDeviceList(String installedAppId) {
        JsonNode response = smartThingsClient.getAllDevices(installedAppId); // deviceId는 null
        return objectMapper.convertValue(response, DeviceListResponse.class);
    }

    // 디바이스 제어 명령어 조회 API
    public JsonNode fetchDeviceCommands(String deviceId, String capabilityId, String installedAppId) {
        return smartThingsClient.fetchDeviceCommands(deviceId, capabilityId, installedAppId);
    }

    // 디바이스 상태 조회
    public JsonNode fetchDeviceStatus(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기기를 찾을 수 없습니다."));

        String controlDeviceId = device.getControlId();
        String installedAppId = device.getInstalledAppId();

        return smartThingsClient.fetchDeviceStatus(controlDeviceId, installedAppId);
    }

    // 전원, 볼륨 등 제어 명령어
    public <T> T executeCommand(Long deviceId, CommandRequest body, Class<T> responseType) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "deviceId에 해당하는 디바이스를 찾을 수 없습니다."));

        String controlDeviceId = String.valueOf(device.getControlId());
        String installedAppId = device.getInstalledAppId();

        // 로그 확인용
        log.debug("----- Log: executeCommand ------ ", "");
        log.debug("-", "[ Node.js API 요청 ]");
        log.debug("-", "- 요청 URL : /devices/" + controlDeviceId + "/command");
        log.debug("-", "- Header(installedappid) : " + installedAppId);
        log.debug("-", "- 내부 body : " + body.toString());
        log.debug("----- Log: executeCommand ------ ", "");

        JsonNode response = smartThingsClient.executeCommand(controlDeviceId, installedAppId, body).getBody();
        return objectMapper.convertValue(response, responseType);
    }
}
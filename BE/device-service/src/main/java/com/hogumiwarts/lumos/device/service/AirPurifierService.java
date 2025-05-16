package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.airpurifier.*;
import com.hogumiwarts.lumos.device.dto.device.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.util.AirQualityLevelUtil;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import com.hogumiwarts.lumos.util.AuthUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirPurifierService {

    private final DeviceRepository deviceRepository;
    private final ExternalDeviceService externalDeviceService;

    // 공기 청정기 onoff
    public AirPurifierStatusResponse updateAirPurifierPower(Long deviceId, PowerControlRequest request) {

        CommandRequest command = DeviceCommandUtil.buildAirPurifierPowerCommand(request.getActivated());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);


        // 2. SmartThings 상태 조회
        JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);

        // Status 파싱
        JsonNode main = raw.path("status").path("components").path("main");

        // 전원 상태
        boolean activated = AirPurifierUtil.parsePower(main);
        boolean success = (activated == request.getActivated());

        return AirPurifierStatusResponse.builder()
                .activated(activated)
                .success(success)
                .build();

    }

    // 공기 청정기 fanmode
    public AirPurifierFanModeResponse updateAirPurifierFanMode(Long deviceId, FanModeControlRequest request) {

        CommandRequest command = DeviceCommandUtil.buildAirPurifierFanModeCommand(request.getFanMode());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);

        // 2. SmartThings 상태 조회
        JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);

        // Status 파싱
        JsonNode main = raw.path("status").path("components").path("main");

        // 팬 속도
        String fanMode = AirPurifierUtil.parseFanMode(main);
        fanMode = capitalizeFirstLetter(fanMode);

        boolean success = request.getFanMode().getMode().equals(fanMode);
        log.info("Fan mode changed to {}, {}", fanMode, success);

        return AirPurifierFanModeResponse.builder()
                .fanMode(fanMode)
                .success(success)
                .build();

    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    public AirPurifierDetailResponse getAirPurifierStatus(Long deviceId) {
        // JWT 기반 인증 정보 가져오기
        Long memberId = AuthUtil.getMemberId();

        // 1. DB에서 디바이스 조회
        Device device = (Device) deviceRepository.findByDeviceIdAndMemberId(deviceId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("기기를 찾을 수 없습니다."));

        // 2. SmartThings 상태 조회
        JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);

        // Status 파싱
        JsonNode main = raw.path("status").path("components").path("main");

        // 전원 상태
        Boolean activated = AirPurifierUtil.parsePower(main);

        // CAQI (공기질 등급: MaxLevel 4)
//		String rawAirQuality = main.path("airQualitySensor").path("airQuality").path("value").asText(null);
        String caqi = AirPurifierUtil.parseCaqi(main);

        // 냄새 수치
        Integer odorLevel = AirPurifierUtil.parseOdorLevel(main);

        // 미세먼지 / 초미세먼지
        Integer dustLevel = AirPurifierUtil.parseDustLevel(main);
        Integer fineDustLevel = AirPurifierUtil.parseFineDustLevel(main);

        // 팬 속도
        String fanMode = AirPurifierUtil.parseFanMode(main);

        // 필터 사용 시간
        Integer filterUsageTime = AirPurifierUtil.parseFilterUsageTime(main);


        return AirPurifierDetailResponse.builder()
                .tagNumber(device.getTagNumber())
                .deviceId(device.getDeviceId())
                .deviceImg(device.getDeviceUrl())
                .deviceName(device.getDeviceName())
                .manufacturerCode(device.getDeviceManufacturer())
                .deviceModel(device.getDeviceModel())
                .deviceType(device.getDeviceType())
                .activated(activated)
                .caqi(caqi)
                .odorLevel(odorLevel)
                .dustLevel(dustLevel)
                .fineDustLevel(fineDustLevel)
                .fanMode(fanMode)
                .filterUsageTime(filterUsageTime)
                .build();
    }
}
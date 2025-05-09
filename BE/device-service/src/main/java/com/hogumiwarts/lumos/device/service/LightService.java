package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.util.AuthUtil;
import com.hogumiwarts.lumos.device.util.ColorUtil;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LightService {

    private final DeviceRepository deviceRepository;
    private final ExternalDeviceService externalDeviceService;

    public LightDetailResponse getLightStatus(Long deviceId) {

        // JWT 기반 인증 정보 가져오기
        Long memberId = AuthUtil.getMemberId();

        // 1. DB에서 디바이스 조회
        Device device = (Device) deviceRepository.findByDeviceIdAndMemberId(deviceId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "deviceId에 해당하는 디바이스를 찾을 수 없습니다."));

        // 2. SmartThings 상태 조회
        JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);

        // Status 파싱
        JsonNode main = raw.path("status")
                .path("components")
                .path("main");


        // 조명 상태
        JsonNode lightNode = main.path("switch").path("switch");
        String lightValue = lightNode.path("value").asText(null);




        // 색온도 : lightTemperature
        int colorTemperature = main
                .path("colorTemperature")
                .path("colorTemperature")
                .path("value")
                .asInt(-1);

        // lightColor : 밝기
        JsonNode brightnessNode = main
                .path("switchLevel")
                .path("level")
                .path("value");

        Integer brightness = null;
        if (!brightnessNode.isMissingNode() && !brightnessNode.isNull()) {
            brightness = brightnessNode.asInt();
            System.out.println("조명 밝기: " + brightness + "%");
        } else {
            System.out.println("밝기 정보 없음");
        }

        // lightCode : hex 값
        JsonNode hueNode = main.path("colorControl").path("hue").path("value");
        JsonNode satNode = main.path("colorControl").path("saturation").path("value");
        String hex = null;
        if (!hueNode.isMissingNode() && !hueNode.isNull()
                && !satNode.isMissingNode() && !satNode.isNull()) {

            double hue = hueNode.asDouble();
            double saturation = satNode.asDouble();

            hex = ColorUtil.hslToHex(hue, saturation);
            System.out.println("HEX Color: " + hex);
        } else {
            System.out.println("색상 정보가 없습니다.");
        }


        return LightDetailResponse.builder()
                .tagNumber(device.getTagNumber())
                .deviceId(device.getDeviceId())
                .deviceImg(device.getDeviceUrl())
                .deviceName(device.getDeviceName())
                .manufacturerCode(device.getDeviceManufacturer())
                .deviceModel(device.getDeviceModel())
                .deviceType(device.getDeviceType())
                .activated("on".equalsIgnoreCase(lightValue))
                .lightColor(String.valueOf(brightness))
                .lightTemperature(String.valueOf(colorTemperature))
                .lightCode(hex)
                .build();
    }

    // 조명 on/off
    public void updateLightStatus(Long deviceId, PowerControlRequest request) {
        CommandRequest command = DeviceCommandUtil.buildLightOnOffCommand(request.getActivated());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
    }

    // 조명 색상 변경
    public void updateLightColor(Long deviceId, LightColorRequest request) {
        CommandRequest command = DeviceCommandUtil.buildLightColorCommand(request.getLightColor());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
    }

    // 조명 색 온도 변경
    public void updateLightTemperature(Long deviceId, LightTemperatureRequest request) {
        CommandRequest command = DeviceCommandUtil.buildLightColorTemperatureCommand(request.getTemperature());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
    }
    
    // 조명 밝기 변경
    public void updateLightBrightness(Long deviceId, LightBrightRequest request) {
        CommandRequest command = DeviceCommandUtil.buildLightColorBrightnessCommand(request.getBrightness());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
    }
}

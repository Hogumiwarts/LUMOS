package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.device.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.dto.light.*;
import com.hogumiwarts.lumos.device.dto.device.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.dto.light.LightBrightRequest;
import com.hogumiwarts.lumos.device.dto.light.LightColorRequest;
import com.hogumiwarts.lumos.device.dto.light.LightDetailResponse;
import com.hogumiwarts.lumos.device.dto.light.LightTemperatureRequest;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LightService {

    private final LightUtil lightUtil;
    private final ExternalDeviceService externalDeviceService;

    public LightDetailResponse getLightStatus(Long deviceId) {

        Long memberId = AuthUtil.getMemberId();
        Device device = lightUtil.getDeviceOrThrow(deviceId, memberId);
        JsonNode main = lightUtil.getMainStatusNode(deviceId);

        String lightValue = lightUtil.parseLightSwitch(main);
        Integer brightness = lightUtil.parseBrightness(main);
        int colorTemperature = lightUtil.parseColorTemperature(main);
        float[] hueSat = lightUtil.parseHueSaturation(main);

        float hue = hueSat[0];
        float saturation = (float) hueSat[1] / 100f;

        return LightDetailResponse.builder()
                .tagNumber(device.getTagNumber())
                .deviceId(device.getDeviceId())
                .deviceImg(device.getDeviceUrl())
                .deviceName(device.getDeviceName())
                .manufacturerCode(device.getDeviceManufacturer())
                .deviceModel(device.getDeviceModel())
                .deviceType(device.getDeviceType())
                .activated("on".equalsIgnoreCase(lightValue))
                .brightness(brightness)
                .lightTemperature(colorTemperature)
                .hue(hue)
                .saturation(saturation)
                .build();
    }

    // 조명 on/off
    public LightPowerResponse updateLightStatus(Long deviceId, PowerControlRequest request) {

        // 1. 디바이스에 ON/OFF 명령 전달
        CommandRequest command = DeviceCommandUtil.buildLightOnOffCommand(request.getActivated());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);

//        // 2. SmartThings 상태 조회
//        JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);
//
//        // Status 파싱
//        JsonNode main = raw.path("status")
//                .path("components")
//                .path("main");
//
//
//        // 조명 상태
//        JsonNode lightNode = main.path("switch").path("switch");
//        String lightValue = lightNode.path("value").asText(null);

        JsonNode main = lightUtil.getMainStatusNode(deviceId);
        String lightValue = lightUtil.parseLightSwitch(main);

        boolean activated = "on".equalsIgnoreCase(lightValue);
        boolean success = request.getActivated() == activated;

        // 3. 결과 반환
        return LightPowerResponse.builder()
                .activated(activated)
                .success(success)
                .build();
    }

    // 조명 색상 변경
    public LightColorResponse updateLightColor(Long deviceId, LightColorRequest request) {

        CommandRequest command = DeviceCommandUtil.buildLightColorCommand(request);
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);

        JsonNode main = lightUtil.getMainStatusNode(deviceId);
        float[] hueSat = lightUtil.parseHueSaturation(main);
        float hue = hueSat[0];
        float saturation = hueSat[1] / 100f;

        boolean success = request.getHue() == hue && request.getSaturation() == saturation;

        return LightColorResponse.builder()
                .hue(hue)
                .saturation(saturation)
                .success(success)
                .build();
    }

    // 조명 색 온도 변경
    public LightTemperatureResponse updateLightTemperature(Long deviceId, LightTemperatureRequest request) {

        CommandRequest command = DeviceCommandUtil.buildLightColorTemperatureCommand(request.getTemperature());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);

        JsonNode main = lightUtil.getMainStatusNode(deviceId);
        int colorTemperature = lightUtil.parseColorTemperature(main);
        boolean success = request.getTemperature() == colorTemperature;

        return LightTemperatureResponse.builder()
                .temperature(colorTemperature)
                .success(success)
                .build();
    }

    // 조명 밝기 변경
    public LightBrightnessResponse updateLightBrightness(Long deviceId, LightBrightRequest request) {

        CommandRequest command = DeviceCommandUtil.buildLightColorBrightnessCommand(request.getBrightness());
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);

        JsonNode main = lightUtil.getMainStatusNode(deviceId);
        Integer brightness = lightUtil.parseBrightness(main);

        boolean success = request.getBrightness() == brightness;

        return LightBrightnessResponse.builder()
                .brightness(brightness)
                .success(success)
                .build();
    }
}
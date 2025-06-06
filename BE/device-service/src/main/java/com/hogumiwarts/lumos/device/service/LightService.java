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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LightService {

    private final LightUtil lightUtil;
    private final ExternalDeviceService externalDeviceService;
    private static final float EPSILON = 0.01f;


    public LightDetailResponse getLightStatus(Long deviceId) {

        Long memberId = AuthUtil.getMemberId();
        Device device = lightUtil.getDeviceOrThrow(deviceId, memberId);
        JsonNode main = lightUtil.getMainStatusNode(deviceId);

        String lightValue = lightUtil.parseLightSwitch(main);
        Integer brightness = lightUtil.parseBrightness(main);
        int colorTemperature = lightUtil.parseColorTemperature(main);
        float[] hueSat = lightUtil.parseHueSaturation(main);

        float hue = hueSat[0];
        float saturation = hueSat[1];

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

        log.info("request: {}, {}", request.getHue(), request.getSaturation());

        CommandRequest command = DeviceCommandUtil.buildLightColorCommand(request);
        externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);

        JsonNode main = lightUtil.getMainStatusNode(deviceId);
        float[] hueSat = lightUtil.parseHueSaturation(main);
        float hue = hueSat[0];
        float saturation = hueSat[1];

        log.info("request: {}, {}", hue, saturation);

        boolean success =
                Math.round(request.getHue()) == Math.round(hue) &&
                        Math.round(request.getSaturation()) == Math.round(saturation);


        return LightColorResponse.builder()
                .hue(request.getHue())
                .saturation(request.getSaturation())
                .success(success)
                .build();
    }
    private boolean floatEquals(float a, float b) {
        return Math.abs(a - b) < EPSILON;
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
package com.hogumiwarts.lumos.device.dto.light;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.service.ExternalDeviceService;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LightUtil {

    private final DeviceRepository deviceRepository;
    private final ExternalDeviceService externalDeviceService;

    public Device getDeviceOrThrow(Long deviceId, Long memberId) {
        return (Device) deviceRepository.findByDeviceIdAndMemberId(deviceId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "deviceId에 해당하는 디바이스를 찾을 수 없습니다."));
    }

    public JsonNode getMainStatusNode(Long deviceId) {
        JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);
        return raw.path("status").path("components").path("main");
    }

    public String parseLightSwitch(JsonNode main) {
        return main.path("switch").path("switch").path("value").asText(null);
    }

    public Integer parseBrightness(JsonNode main) {
        JsonNode brightnessNode = main.path("switchLevel").path("level").path("value");
        return (brightnessNode.isMissingNode() || brightnessNode.isNull()) ? null : brightnessNode.asInt();
    }

    public int parseColorTemperature(JsonNode main) {
        return main.path("colorTemperature").path("colorTemperature").path("value").asInt(-1);
    }

    public float[] parseHueSaturation(JsonNode main) {
        JsonNode hueNode = main.path("colorControl").path("hue").path("value");
        JsonNode satNode = main.path("colorControl").path("saturation").path("value");

        if (!hueNode.isMissingNode() && !hueNode.isNull()
                && !satNode.isMissingNode() && !satNode.isNull()) {
            return new float[]{ hueNode.floatValue(), satNode.floatValue() };
        }
        return new float[]{-1f, -1f};
    }
}



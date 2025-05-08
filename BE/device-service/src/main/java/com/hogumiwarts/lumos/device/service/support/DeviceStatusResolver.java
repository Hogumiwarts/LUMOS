package com.hogumiwarts.lumos.device.service.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.service.ExternalDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceStatusResolver {

    private final ExternalDeviceService externalDeviceService;

    public boolean getActivatedStatus(Device device) {
        try {
            JsonNode raw = externalDeviceService.fetchDeviceStatus(device.getDeviceId());
            JsonNode main = raw.path("status").path("components").path("main");

            return switch (device.getDeviceType()) {
                case "SWITCH" -> "on".equalsIgnoreCase(main.path("switch").path("switch").path("value").asText(""));
                case "AUDIO" -> {
                    String val = main.path("mediaPlayback").path("playbackStatus").path("value").asText("");
                    yield switch (val.toLowerCase()) {
                        case "playing", "fast forwarding", "rewinding" -> true;
                        default -> false;
                    };
                }
                case "AIRPURIFIER" -> "on".equalsIgnoreCase(main.path("switch").path("switch").path("value").asText(""));
//                case "AUDIO" -> "playing".equalsIgnoreCase(main.path("audioPlayback").path("playbackStatus").path("value").asText(""));
                default -> false;
            };
        } catch (Exception e) {
            // 조회 실패 시 기본값 false
            return false;
        }
    }
}
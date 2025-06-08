package com.hogumiwarts.lumos.device.entity;

import java.util.List;
import java.util.Map;

public enum DeviceType {
    SWITCH,
    AUDIO,
    AIRPURIFIER,
    LIGHT,
    UNKNOWN;

    public static DeviceType fromModelName(String deviceModel) {
        if (deviceModel == null) return UNKNOWN;
        String name = deviceModel.toLowerCase();

        // Switch (푸시 버튼)
        if (name.contains("push") || name.contains("mini")) return SWITCH;

        // Air Purifier
        if (name.contains("air") || name.contains("purifier") || name.contains("airpurifier")
                || name.contains("artik")) return AIRPURIFIER;

        // Audio / Speaker
        if (name.contains("bookshelf") || name.contains("sonos")
                || name.contains("speaker") || name.contains("audio") || name.contains("sound") || name.contains("쉼포니") || name.contains("player"))
            return AUDIO;

        // Light
        if (name.contains("light") || name.contains("lamp") || name.contains("bulb")
                || name.contains("lite_lab") || name.contains("조명") || name.contains("전구")
                || name.contains("lighting") || name.contains("hero"))
            return LIGHT;

        return UNKNOWN;
    }

    public Map<String, Object> defaultControlInfo() {
        return switch (this) {
            case SWITCH -> Map.of(
                    "capabilities", List.of(
                            Map.of(
                                    "capability", "switch",
                                    "commands", List.of("on", "off")
                            )
                    )
            );
            case AIRPURIFIER -> Map.of(
                    "capabilities", List.of(
                            Map.of(
                                    "capability", "switch",
                                    "commands", List.of("on", "off")
                            )
                    )
            );
            case AUDIO -> Map.of(
                    "capabilities", List.of(
                            Map.of(
                                    "capability", "audioVolume",
                                    "commands", List.of("volumeUp", "volumeDown", "setVolume")
                            ),
                            Map.of(
                                    "capability", "mediaPlayback",
                                    "commands", List.of("play", "pause", "stop", "rewind", "fastForward")
                            )
                    )
            );
            case LIGHT -> Map.of(
                    "capabilities", List.of(
                            Map.of(
                                    "capability", "switch",
                                    "commands", List.of("on", "off")
                            ),
                            Map.of(
                                    "capability", "colorControl",
                                    // hue: 색조(0=빨강, 120=초록, 240=파랑)
                                    // saturation: 채도(색상의 강도 / 0: 회색 ~ 100: 완전한 색상)
                                    "commands", List.of("hex", "hue", "saturation")
                            )
                    )
            );
            case UNKNOWN -> Map.of();
        };
    }
}
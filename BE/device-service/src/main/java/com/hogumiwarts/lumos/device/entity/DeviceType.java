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
        if (name.contains("push") || name.contains("mini")) return SWITCH;
        if (name.contains("air") || name.contains("purifier")) return AIRPURIFIER;
        if (name.contains("bookshelf") || name.contains("sonos")) return AUDIO;
        if (name.contains("lite_lab")) return LIGHT;
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

            // Example
            case LIGHT -> Map.of(
                    "capabilities", List.of(
                            Map.of(
                                    "capability", "switch",
                                    "commands", List.of("on", "off")
                            ),
                            Map.of(
                                    "capability", "switchLevel",
                                    "commands", List.of("setLevel")
                            )
                    )
            );
            case UNKNOWN -> Map.of();
        };
    }
}

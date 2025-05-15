package com.hogumiwarts.lumos.device.dto.airpurifier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = "공기청정기 팬 모드")
public enum FanMode {
    @Schema(description = "자동 모드") AUTO("auto"),
    @Schema(description = "약풍") LOW("low"),
    @Schema(description = "중간풍") MEDIUM("medium"),
    @Schema(description = "강풍") HIGH("high"),
    @Schema(description = "조용한 모드") QUIET("quiet");

    private final String mode;

    FanMode(String mode) {
        this.mode = mode;
    }

    @JsonValue
    public String getMode() {
        return mode;
    }

    @JsonCreator
    public static FanMode from(String input) {
        return Arrays.stream(FanMode.values())
                .filter(f -> f.mode.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid fan mode: " + input));
    }
}

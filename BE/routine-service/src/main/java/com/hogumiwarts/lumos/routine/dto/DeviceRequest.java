package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceRequest {

    @Schema(description = "디바이스 ID", example = "1")
    private Long deviceId;

    @Schema(description = "SmartThings 제어용 installedAppId", example = "스마트 전등")
    private String installedAppId;

    @Schema(description = "SmartThings 제어용 deviceId", example = "https://cdn.example.com/img/light.png")
    private String controlId;

    @Schema(description = "디바이스 제어 명령 목록", example = """
        [
            {
                "component": "main",
                capability": "switch",
                "command": "on",
                "arguments": []
            }
        ]
        """)
    private List<CommandRequest> commands;
}

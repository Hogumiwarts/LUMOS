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
public class DevicesCreateRequest {

    @Schema(description = "디바이스 ID", example = "1")
    private Long deviceId;

    @Schema(description = "SmartThings 제어용 installedAppId", example = "5f810cf2-432c-4c4c-bc72-c5af5abf1ef5")
    private String installedAppId;

    @Schema(description = "SmartThings 제어용 deviceId", example = "127cea68-8088-44bb-be53-b14a70e12660")
    private String controlId;

    @Schema(description = "디바이스 제어 명령 목록")
    private List<CommandRequest> commands;
}

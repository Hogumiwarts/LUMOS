package com.hogumiwarts.lumos.routine.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandRequest {

    @Schema(description = "SmartThings 컴포넌트", example = "main")
    private String component;

    @Schema(description = "SmartThings capability", example = "switch")
    private String capability;

    @Schema(description = "실행할 명령", example = "on")
    private String command;

    @Schema(description = "명령 인자", example = "[]")
    private List<Object> arguments;
}

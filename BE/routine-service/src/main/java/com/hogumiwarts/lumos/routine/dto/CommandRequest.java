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

    private Long component;
    private String capability;
    private String command;
    private List<Map<String, ?>> arguments;
}

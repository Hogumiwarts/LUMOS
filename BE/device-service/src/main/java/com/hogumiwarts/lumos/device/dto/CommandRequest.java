package com.hogumiwarts.lumos.device.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequest {
    private List<Command> commands;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Command {
        private String component;
        private String capability;
        private String command;
        private List<Object> arguments;
    }

}

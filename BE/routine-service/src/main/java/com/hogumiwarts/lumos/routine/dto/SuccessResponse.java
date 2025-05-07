package com.hogumiwarts.lumos.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SuccessResponse {
    private boolean success;
}

package com.hogumiwarts.lumos.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VolumeControlResponse {
    private Long memberId;
    private int volume;
}
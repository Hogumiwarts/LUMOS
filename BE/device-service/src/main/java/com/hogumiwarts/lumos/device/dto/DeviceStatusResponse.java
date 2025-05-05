package com.hogumiwarts.lumos.device.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceStatusResponse {
    private int tagNumber;
    private Long deviceId;
    private String deviceImg;
    private String deviceName;
    private boolean activated;
}
package com.hogumiwarts.lumos.device.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceStatusResponse {
    private Integer tagNumber;
    private Long deviceId;
    private String installedAppId;
    private String deviceImg;
    private String deviceName;
    private boolean activated;
}
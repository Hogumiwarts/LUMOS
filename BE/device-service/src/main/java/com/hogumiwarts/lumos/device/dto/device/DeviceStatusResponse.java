package com.hogumiwarts.lumos.device.dto.device;

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
    private String deviceType;
    private boolean activated;
}
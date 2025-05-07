package com.hogumiwarts.lumos.device.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmartThingsDevice {
    private String deviceId;
    private String label;
    private String name;
    private String deviceModel;
    private String deviceManufacturerCode;
}
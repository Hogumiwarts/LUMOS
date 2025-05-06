package com.hogumiwarts.lumos.device.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewDiscoveredDeviceResponse {
    private String controlId;     // SmartThings deviceId
    private String deviceName;    // SmartThings label
    private String deviceModel;   // 모델명 (선택)
    private String deviceImg;     // 추후 필요시 URL 지정
}
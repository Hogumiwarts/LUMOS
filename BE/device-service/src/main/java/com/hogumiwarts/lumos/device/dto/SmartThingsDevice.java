package com.hogumiwarts.lumos.device.dto;

import com.fasterxml.jackson.databind.JsonNode;
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

    // OCF 기반 모델정보를 위해 추가
    private JsonNode ocf;
    private JsonNode samsungce_deviceIdentification; // JSON 필드 이름이 .일 경우 언더스코어로 대체
}
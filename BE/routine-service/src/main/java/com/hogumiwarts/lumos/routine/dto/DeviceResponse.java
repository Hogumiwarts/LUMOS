package com.hogumiwarts.lumos.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponse {
    private Integer tagNumber;
    private Long deviceId;
    private String installedAppId;
    private String deviceImg;       // deviceUrl 사용
    private String deviceName;
    private String deviceType;
    private Boolean activated;      // control JSON에서 추출하거나 별도 컬럼일 경우 직접 사용
}
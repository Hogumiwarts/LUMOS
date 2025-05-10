package com.hogumiwarts.lumos.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicesCreateResponse {
	private Long deviceId;
	private String deviceName;
	private String deviceType;
	private String deviceImageUrl;
}

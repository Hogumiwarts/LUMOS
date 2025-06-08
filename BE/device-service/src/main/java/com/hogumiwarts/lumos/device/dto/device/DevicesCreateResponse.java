package com.hogumiwarts.lumos.device.dto.device;

import java.util.Optional;

import com.hogumiwarts.lumos.device.entity.Device;

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
	private String installedAppId;
	private String controlId;

	public static DevicesCreateResponse from(Device device) {
		return DevicesCreateResponse.builder()
			.deviceId(device.getDeviceId())
			.deviceName(device.getDeviceName())
			.deviceType(device.getDeviceType())
			.deviceImageUrl(device.getDeviceUrl())
			.installedAppId(device.getInstalledAppId())
			.controlId(device.getControlId())
			.build();
	}
}

package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AudioService {

	private final ExternalDeviceService externalDeviceService;

	public JsonNode getAudioStatus(Long deviceId, Long memberId) {
		return externalDeviceService.fetchDeviceStatus(deviceId);
	}

	// buildAudioVolumeCommand
	public void updateAudioVolume(Long deviceId, VolumeControlRequest request) {
		CommandRequest command = DeviceCommandUtil.buildAudioVolumeCommand(request.getVolume());
		externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
	}

	public void updateAudioPlayback(Long deviceId, PowerControlRequest request) {
		CommandRequest command = DeviceCommandUtil.buildAudioPlayBackCommand(request.getActivated());
		externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
	}



}
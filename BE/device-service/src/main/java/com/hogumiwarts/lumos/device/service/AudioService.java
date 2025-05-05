package com.hogumiwarts.lumos.device.service;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AudioService {

	private final ExternalDeviceService externalDeviceService;

	public DeviceStatusResponse getAudioStatus(Long deviceId, Long memberId) {
		return externalDeviceService.fetchDeviceStatus(deviceId);
	}

	public VolumeControlResponse updateAudioVolume(Long deviceId, VolumeControlRequest request) {
		return externalDeviceService.executeCommand(deviceId, request, VolumeControlResponse.class);
	}


}
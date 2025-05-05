package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.AudioApiSpec;
import com.hogumiwarts.lumos.device.docs.DeviceApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.service.AudioService;
import com.hogumiwarts.lumos.device.service.DeviceService;
import com.hogumiwarts.lumos.dto.CommonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class AudioController implements AudioApiSpec {

	private final DeviceService deviceService;
	private final AudioService audioService;

	// TODO : 공통응답 적용, Request, Response 구조, Error 처리
	@Override
	public ResponseEntity<CommonResponse<DeviceStatusResponse>> updateAudioPower(Long deviceId, PowerControlRequest request) {
		DeviceStatusResponse response = deviceService.executeCommand(deviceId, request);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

	// TODO : 공통응답 적용, Request, Response 구조, Error 처리
	@Override
	public ResponseEntity<?> updateAudioVolume(Long deviceId, VolumeControlRequest request) {
		VolumeControlResponse response = audioService.updateAudioVolume(deviceId, request);
		return ResponseEntity.ok(response);
	}

	// TODO : 공통응답 적용, Request, Response 구조, Error 처리
	@Override
	public ResponseEntity<?> getAudioStatus(Long deviceId, Long memberId) {
		DeviceStatusResponse response = audioService.getAudioStatus(deviceId, memberId);
		return ResponseEntity.ok(response);
	}

}
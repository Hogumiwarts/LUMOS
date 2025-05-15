package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.AudioApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.audio.AudioDetailResponse;
import com.hogumiwarts.lumos.device.dto.audio.VolumeControlRequest;
import com.hogumiwarts.lumos.device.dto.audio.AudioDetailResponse;
import com.hogumiwarts.lumos.device.dto.audio.AudioPlaybackResponse;
import com.hogumiwarts.lumos.device.dto.audio.AudioVolumnResponse;
import com.hogumiwarts.lumos.device.dto.audio.VolumeControlRequest;
import com.hogumiwarts.lumos.device.service.AudioService;
import com.hogumiwarts.lumos.dto.CommonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class AudioController implements AudioApiSpec {

	private final AudioService audioService;

	@Override
	public ResponseEntity<CommonResponse<AudioVolumnResponse>> updateAudioVolume(Long deviceId, VolumeControlRequest request) {
		AudioVolumnResponse response = audioService.updateAudioVolume(deviceId, request);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

	@Override
	public ResponseEntity<CommonResponse<AudioPlaybackResponse>> updateAudioPlayback(Long deviceId, PowerControlRequest request) {
		AudioPlaybackResponse response = audioService.updateAudioPlayback(deviceId, request);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

	@Override
	public ResponseEntity<CommonResponse<AudioDetailResponse>> getAudioStatus(Long deviceId) {
		AudioDetailResponse response = audioService.getAudioStatus(deviceId);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

}
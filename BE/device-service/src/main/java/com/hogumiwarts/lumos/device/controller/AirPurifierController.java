package com.hogumiwarts.lumos.device.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.docs.AirPurifierApiSpec;
import com.hogumiwarts.lumos.device.docs.AudioApiSpec;
import com.hogumiwarts.lumos.device.dto.PowerControlRequest;
import com.hogumiwarts.lumos.device.dto.SuccessResponse;
import com.hogumiwarts.lumos.device.dto.SwitchDetailResponse;
import com.hogumiwarts.lumos.device.dto.VolumeControlRequest;
import com.hogumiwarts.lumos.device.service.AirPurifierService;
import com.hogumiwarts.lumos.device.service.AudioService;
import com.hogumiwarts.lumos.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class AirPurifierController implements AirPurifierApiSpec {

	private final AirPurifierService airPurifierService;

	@Override
	public ResponseEntity<CommonResponse<SuccessResponse>> updateAirPurifierPower(Long deviceId, PowerControlRequest request) {
		airPurifierService.updateAirPurifierPower(deviceId, request);
		return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
	}

	@Override
	public ResponseEntity<CommonResponse<?>> getAirPurifierStatus(Long deviceId, Long memberId) {
		Object response = airPurifierService.getAirPurifierStatus(deviceId, memberId);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

}
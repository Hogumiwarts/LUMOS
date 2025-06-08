package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.SwitchApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.minibig.SwitchDetailResponse;
import com.hogumiwarts.lumos.device.dto.minibig.SwitchDetailResponse;
import com.hogumiwarts.lumos.device.dto.minibig.SwitchStatusResponse;
import com.hogumiwarts.lumos.device.service.SwitchService;
import com.hogumiwarts.lumos.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class SwitchController implements SwitchApiSpec {

	private final SwitchService switchService;

	@Override
	public ResponseEntity<CommonResponse<SwitchStatusResponse>> updateSwitchPower(Long deviceId, PowerControlRequest request) {
		SwitchStatusResponse response = switchService.updateSwitchPower(deviceId, request);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

	@Override
	public ResponseEntity<CommonResponse<SwitchDetailResponse>> getSwitchStatus(Long deviceId) {
		SwitchDetailResponse response = switchService.getSwitchStatus(deviceId);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

}
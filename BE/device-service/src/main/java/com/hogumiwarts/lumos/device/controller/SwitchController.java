package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.DeviceApiSpec;
import com.hogumiwarts.lumos.device.docs.SwitchApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.service.DeviceService;
import com.hogumiwarts.lumos.device.service.SwitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class SwitchController implements SwitchApiSpec {

	private final DeviceService deviceService;
	private final SwitchService switchService;

	// TODO : 공통응답 적용, Request, Response 구조, Error 처리
	@Override
	public ResponseEntity<?> updateSwitchPower(Long deviceId, PowerControlRequest request) {
		DeviceStatusResponse response = deviceService.executeCommand(deviceId, request);
		return ResponseEntity.ok(response);
	}

	// TODO : 공통응답 적용, Request, Response 구조, Error 처리
	@Override
	public ResponseEntity<?> getSwitchStatus(Long deviceId, Long memberId) {
		DeviceStatusResponse response = switchService.getSwitchStatus(deviceId, memberId);
		return ResponseEntity.ok(response);
	}

}
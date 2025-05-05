package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.DeviceApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController implements DeviceApiSpec {

	private final DeviceService deviceService;

	// TODO : 공통응답 적용
	@Override
	public ResponseEntity<List<DeviceResponse>> getAllDeviceByMember(Long memberId) {
		List<DeviceResponse> devices = deviceService.getAllDeviceByMember(memberId);
		return ResponseEntity.ok(devices);
	}

	// TODO : 공통응답 적용, Error 처리, 응답결과가 0인 경우, 새로운 DB 항목 발견시, 추가 Node.js 요청으로 제어 목록 받아오기
	@Override
	public ResponseEntity<List<NewDiscoveredDeviceResponse>> getSmartThingsDevices(Long memberId) {
		String installedAppId = "11237955-08ec-418f-a2cd-8c7ca1a0919e"; // 테스트용 하드코딩
		List<NewDiscoveredDeviceResponse> result = deviceService.getSmartThingsDevices(memberId, installedAppId);
		return ResponseEntity.ok(result);
	}

	// TODO : 공통응답 적용, Request, Response 구조, Error 처리
	@Override
	public ResponseEntity<?> getDeviceStatusByTagNumber(int tagNumber, Long memberId) {
		DeviceStatusResponse response = deviceService.getDeviceStatusByTagNumber(tagNumber, memberId);
		return ResponseEntity.ok(response);
	}

}
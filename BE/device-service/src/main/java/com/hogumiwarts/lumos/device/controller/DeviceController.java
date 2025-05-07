package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.DeviceApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.service.DeviceService;
import com.hogumiwarts.lumos.dto.CommonResponse;

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

	// TODO : Main 에서 보여줘야할 정보 확인 필요
	@Override
	public ResponseEntity<CommonResponse<List<DeviceStatusResponse>>> getAllDeviceByMember() {
		List<DeviceStatusResponse> devices = deviceService.getAllDeviceByMember();
		String message = devices.isEmpty() ? "등록된 디바이스가 없습니다." : "기기 목록 조회 성공";
		return ResponseEntity.ok(CommonResponse.ok(message, devices));
	}

	@Override
	public ResponseEntity<CommonResponse<List<DeviceStatusResponse>>> getSmartThingsDevices(Long memberId) {
		// TODO 삭제 : 임시 installedAppId, 테스트를 위해 하드코딩
		String installedAppId = "5f810cf2-432c-4c4c-bc72-c5af5abf1ef5";

		List<DeviceStatusResponse> result = deviceService.getSmartThingsDevices(memberId, installedAppId);
		String message = result.isEmpty() ? "새롭게 검색된 디바이스가 없습니다." : "기기 목록 조회 성공";
		return ResponseEntity.ok(CommonResponse.ok(message, result));
	}

	@Override
	public ResponseEntity<CommonResponse<Object>> getDeviceStatusByTagNumber(int tagNumber, Long memberId) {
		Object response = deviceService.getDeviceStatusByTagNumber(tagNumber, memberId);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

}
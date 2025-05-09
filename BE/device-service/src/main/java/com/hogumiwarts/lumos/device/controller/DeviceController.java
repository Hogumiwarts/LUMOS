package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.DeviceApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.service.DeviceService;
import com.hogumiwarts.lumos.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController implements DeviceApiSpec {

	private final DeviceService deviceService;

	@Override
	public ResponseEntity<CommonResponse<List<DeviceStatusResponse>>> getAllDeviceByMember() {
		List<DeviceStatusResponse> result = deviceService.getAllDeviceByMember();
		return ResponseEntity.ok(
				result.isEmpty()
						? CommonResponse.ok("등록된 디바이스 정보가 없습니다.", result)
						: CommonResponse.ok("조회 성공", result)
		);
	}

	@Override
	public ResponseEntity<CommonResponse<List<DeviceStatusResponse>>> getSmartThingsDevices(@RequestParam String installedAppId) {
		List<DeviceStatusResponse> result = deviceService.getSmartThingsDevices(installedAppId);
		return ResponseEntity.ok(
				result.isEmpty()
					? CommonResponse.ok("새롭게 검색된 디바이스가 없습니다.", result)
					: CommonResponse.ok("조회 성공", result)
		);
	}

	@Override
	public ResponseEntity<CommonResponse<Object>> getDeviceStatusByTagNumber(int tagNumber) {
		Object response = deviceService.getDeviceStatusByTagNumber(tagNumber);
		return ResponseEntity.ok(CommonResponse.ok(response));
	}

}
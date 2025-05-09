package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.util.AuthUtil;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirPurifierService {

	private final DeviceRepository deviceRepository;
	private final ExternalDeviceService externalDeviceService;

	public void updateAirPurifierPower(Long deviceId, PowerControlRequest request) {
		CommandRequest command = DeviceCommandUtil.buildAirPurifierPowerCommand(request.getActivated());
		externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
	}

	// Todo: 공기청정기 테스트 후, Response 변경 필요
	public SwitchDetailResponse getAirPurifierStatus(Long deviceId) {
		// JWT 기반 인증 정보 가져오기
		Long memberId = AuthUtil.getMemberId();

		// 1. DB에서 디바이스 조회
		Device device = (Device) deviceRepository.findByDeviceIdAndMemberId(deviceId, memberId)
				.orElseThrow(() -> new EntityNotFoundException("기기를 찾을 수 없습니다."));

		// 2. SmartThings 상태 조회
		JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);

		// Status 파싱
		JsonNode main = raw.path("status")
				.path("components")
				.path("main");

		// Battery 상태
//		JsonNode batteryNode = main.path("battery").path("battery");
//		Integer batteryValue = batteryNode.path("value").isInt() ? batteryNode.get("value").asInt() : null;
//		// String batteryTime = batteryNode.path("timestamp").asText(null);

		// Switch 상태
		JsonNode switchNode = main.path("switch").path("switch");
		String switchValue = switchNode.path("value").asText(null);
		String switchTime = switchNode.path("timestamp").asText(null);


		return SwitchDetailResponse.builder()
				.tagNumber(device.getTagNumber())
				.deviceId(device.getDeviceId())
				.deviceImg(device.getDeviceUrl())
				.deviceName(device.getDeviceName())
				.manufacturerCode(device.getDeviceManufacturer())
				.deviceModel(device.getDeviceModel())
				.activated("on".equalsIgnoreCase(switchValue))
//				.switchTimestamp(switchTimestamp)
//				.batteryLevel(batteryLevel)
				.build();
	}



}
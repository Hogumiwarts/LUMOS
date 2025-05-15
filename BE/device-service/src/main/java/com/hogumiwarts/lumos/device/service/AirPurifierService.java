package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierDetailResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.FanModeControlRequest;
import com.hogumiwarts.lumos.device.dto.device.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.util.AirQualityLevelUtil;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import com.hogumiwarts.lumos.util.AuthUtil;

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


	public void updateAirPurifierFanMode(Long deviceId, FanModeControlRequest request) {
		CommandRequest command = DeviceCommandUtil.buildAirPurifierFanModeCommand(request.getFanMode());
		externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
	}


	public AirPurifierDetailResponse getAirPurifierStatus(Long deviceId) {
		// JWT 기반 인증 정보 가져오기
		Long memberId = AuthUtil.getMemberId();

		// 1. DB에서 디바이스 조회
		Device device = (Device) deviceRepository.findByDeviceIdAndMemberId(deviceId, memberId)
			.orElseThrow(() -> new EntityNotFoundException("기기를 찾을 수 없습니다."));

		// 2. SmartThings 상태 조회
		JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);

		// Status 파싱
		JsonNode main = raw.path("status").path("components").path("main");

		// 전원 상태
		String switchValue = main.path("switch").path("switch").path("value").asText(null);

		// CAQI (공기질 등급: MaxLevel 4)
		String rawAirQuality = main.path("airQualitySensor").path("airQuality").path("value").asText(null);
		String caqi = AirQualityLevelUtil.toAirQualityLevel(rawAirQuality);

		// 냄새 수치
		Integer odorLevel = main.path("odorSensor").path("odorLevel").path("value").isInt()
			? main.path("odorSensor").path("odorLevel").path("value").asInt() : null;

		// 미세먼지 / 초미세먼지
		Integer dustLevel = main.path("dustSensor").path("dustLevel").path("value").isInt()
			? main.path("dustSensor").path("dustLevel").path("value").asInt() : null;
		Integer fineDustLevel = main.path("dustSensor").path("fineDustLevel").path("value").isInt()
			? main.path("dustSensor").path("fineDustLevel").path("value").asInt() : null;

		// 팬 속도
		String fanMode = main.path("airConditionerFanMode").path("fanMode").path("value").asText(null);

		// 필터 사용 시간
		Integer filterUsageTime = main.path("custom.filterUsageTime").path("usageTime").path("value").isInt()
			? main.path("custom.filterUsageTime").path("usageTime").path("value").asInt() : null;


		return AirPurifierDetailResponse.builder()
			.tagNumber(device.getTagNumber())
			.deviceId(device.getDeviceId())
			.deviceImg(device.getDeviceUrl())
			.deviceName(device.getDeviceName())
			.manufacturerCode(device.getDeviceManufacturer())
			.deviceModel(device.getDeviceModel())
			.deviceType(device.getDeviceType())
			.activated("on".equalsIgnoreCase(switchValue))
			.caqi(caqi)
			.odorLevel(odorLevel)
			.dustLevel(dustLevel)
			.fineDustLevel(fineDustLevel)
			.fanMode(fanMode)
			.filterUsageTime(filterUsageTime)
			.build();
	}
}
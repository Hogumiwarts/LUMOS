package com.hogumiwarts.lumos.device.service;

import com.hogumiwarts.lumos.device.dto.device.DeviceListResponse;
import com.hogumiwarts.lumos.device.dto.device.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.dto.device.DevicesCreateResponse;
import com.hogumiwarts.lumos.device.dto.device.SmartThingsDevice;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.entity.DeviceType;
import com.hogumiwarts.lumos.device.service.support.DeviceStatusResolver;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

	private final DeviceRepository deviceRepository;
	private final DeviceStatusResolver deviceStatusResolver;

	private final ExternalDeviceService externalDeviceService;

	private final SwitchService switchService;
	private final AudioService audioService;
	private final LightService lightService;
	private final AirPurifierService airPurifierService;

	// [ SmartThingsApp 에 등록된 모든 기기 정보 불러오기 (node.js 통신) ]
	public List<DeviceStatusResponse> getSmartThingsDevices(String installedAppId) {

		log.debug("----- Log: getSmartThingsDevices ------ ", "");
		log.debug("- 인증키: ", installedAppId);

		// 1. SmartThings API 호출
		DeviceListResponse response = externalDeviceService.fetchDeviceList(installedAppId);

		// 2. DB에서 DeviceId 추출 (새로 검색된 디바이스, 이미 등록되어있는 디바이스 비교를 위해)
		Long memberId = AuthUtil.getMemberId();
//		Set<String> existingControlIds = deviceRepository.findByMemberId(memberId)
//			.orElse(new ArrayList<>())
//			.stream()
//			.map(Device::getControlId)
//			.collect(Collectors.toSet());
		List<Device> existingDevices = deviceRepository.findByMemberId(memberId)
				.orElse(new ArrayList<>());

		// 3. 신규 디바이스 조회
		List<DeviceStatusResponse> newlyAdded = new ArrayList<>();
		for (SmartThingsDevice dto : response.getDevices()) {
			String controlId = dto.getDeviceId();

			// 3-1. 기본 모델명 (일반 디바이스)
			String model = dto.getDeviceModel();

			// 3-2. OCF 기반 디바이스 (삼성 기기 등)는 기본 모델명이 없으므로 보완
			if ((model == null || model.isBlank()) && dto.getOcf() != null) {
				String ocfModel = dto.getOcf().path("modelNumber").asText(null);

				if (ocfModel != null && !ocfModel.isBlank()) {
					model = ocfModel.contains("|") ? ocfModel.split("\\|")[0] : ocfModel;
				}
			}

			// 3-3. 이미 등록된 controlId 이면 스킵
			// if (existingControlIds.contains(controlId)) continue;
			// 단, 아래 코드는 manufactureCode 값이 기기마다 고유한 경우일 때 유효. 기기 재등록시 값이 변하게될 경우 다른 고유한 값을 찾아야함
			// 예를 들어 조명 2개가 존재하는데, 각 조명의 manufacturerCode 값이 동일 할 경우 겹치지 않는 다른 변수 값을 활용해야 함
			String manufacturerCode = dto.getDeviceManufacturerCode();
			Device matchedDevice = existingDevices.stream()
					.filter(dev -> manufacturerCode != null && manufacturerCode.equals(dev.getDeviceManufacturer()))
					.findFirst()
					.orElse(null);

			if (matchedDevice != null) {
				// 기존 디바이스인데 controlId가 달라졌다면 갱신
				if (!matchedDevice.getControlId().equals(controlId)) {
					matchedDevice.setControlId(controlId);
					matchedDevice.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
					deviceRepository.save(matchedDevice);
				}

				continue; // 이미 등록된 디바이스 → 스킵
			}

			// 3-4. deviceModel에 "smartTag" (대소문자 무관) 포함되면 스킵
			//		smartTag 는 UWB 활용을 위해 등록한 것이기 때문에 관리대상 디바이스가 아님
			if (model != null) {
				String lowerModel = model.toLowerCase();
				if (lowerModel.contains("smarttag") || lowerModel.contains("hub") || model.contains("허브")) continue;
			}

			// 3-5. 신규 디바이스 저장 : PostgreSQL
			// Control 정보 가져오기
			DeviceType inferredType = DeviceType.fromModelName(model);
			Map<String, Object> controlInfo = inferredType.defaultControlInfo();

			Device device = Device.builder()
				.memberId(memberId)
				.installedAppId(installedAppId)
				.controlId(controlId)
				.deviceName(dto.getLabel())
				.deviceUrl(null)
				.control(controlInfo)
				.updatedAt(Timestamp.valueOf(LocalDateTime.now()))
				.deviceManufacturer(dto.getDeviceManufacturerCode())
				.deviceModel(model)
				.deviceType(inferredType.name())
				.build();
			deviceRepository.save(device);

			// 3-6. 응답용 DTO 생성 : 새롭게 추가된 디바이스 정보만 넘겨줌
			newlyAdded.add(DeviceStatusResponse.builder()
				.tagNumber(null)
				.deviceId(device.getDeviceId())
				.installedAppId(installedAppId)
				.deviceName(device.getDeviceName())
				.deviceType(inferredType.name())
				.activated(false)
				.build());
		}

		return newlyAdded;
	} // ...getSmartThingsDevices()


	// [ DB에 저장된 사용자의 기기 목록 불러오기 ]
	public List<DeviceStatusResponse> getAllDeviceByMember() {
		// JWT 기반 인증 정보 가져오기
		Long memberId = AuthUtil.getMemberId();

		List<Device> devices = deviceRepository.findByMemberId(memberId).orElse(new ArrayList<>());

		return devices.stream()
			.map(device -> {
				boolean activated = deviceStatusResolver.getActivatedStatus(device);
				return DeviceStatusResponse.builder()
					.tagNumber(device.getTagNumber())
					.deviceId(device.getDeviceId())
					.installedAppId(device.getInstalledAppId())
					.deviceImg(device.getDeviceUrl())
					.deviceName(device.getDeviceName())
					.deviceType(device.getDeviceType())
					.activated(activated)
					.build();
			})
			.collect(Collectors.toList());
	}

	// control JSON에서 activated 값 추출 (예: {"activated": true})
	private Boolean extractActivatedFromControl(Map<String, Object> control) {
		if (control == null) return false;
		Object value = control.get("activated");
		return value instanceof Boolean ? (Boolean) value : false;
	}


	// UWB : TagNumber 로 기기 정보 찾기
	public Object getDeviceStatusByTagNumber(int tagNumber) {
		// JWT 기반 인증 정보 가져오기
		Long memberId = AuthUtil.getMemberId();

		// 1. TagNumber 로 Device 정보 찾기
		Device device = deviceRepository.findByTagNumberAndMemberId(tagNumber, memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

		DeviceType inferredType = DeviceType.fromModelName(device.getDeviceModel());
		return switch (inferredType) {
			case SWITCH -> switchService.getSwitchStatus(device.getDeviceId());
			case AUDIO -> audioService.getAudioStatus(device.getDeviceId());
			case LIGHT -> lightService.getLightStatus(device.getDeviceId());
			case AIRPURIFIER -> airPurifierService.getAirPurifierStatus(device.getDeviceId());
			default -> throw new IllegalArgumentException("지원하지 않는 디바이스 타입입니다: " + inferredType);
		};
	}

	public List<DevicesCreateResponse> getDeviceDetailsByIds(List<Long> deviceIds) {
		List<Device> devices = deviceRepository.findAllById(deviceIds);

		return devices.stream()
			.map(DevicesCreateResponse::from)
			.toList();
	}
}
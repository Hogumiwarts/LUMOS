package com.hogumiwarts.lumos.device.service;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.entity.DeviceType;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hogumiwarts.lumos.device.entity.DeviceType.AUDIO;
import static com.hogumiwarts.lumos.device.entity.DeviceType.SWITCH;

@Service
@RequiredArgsConstructor
public class DeviceService {

	private final DeviceRepository deviceRepository;
	private final ExternalDeviceService externalDeviceService;

	private final SwitchService switchService;
	private final AudioService audioService;

	// [ SmartThingsApp 에 등록된 모든 기기 정보 불러오기 (node.js 통신) ]
	public List<DeviceStatusResponse> getSmartThingsDevices(Long memberId, String installedAppId){
		// 1. SmartThings API : 디바이스 목록 불러오기
		DeviceListResponse response = externalDeviceService.fetchDeviceList(installedAppId);

		// 2. 현재 DB에 저장된 MemberId 의 디바이스 목록 불러오기
		Set<String> existingControlIds = deviceRepository.findByMemberId(memberId).stream()
				.map(Device::getControlId)
				.collect(Collectors.toSet());

		// 3. 신규 디바이스만 저장 및 응답 변수 생성
		List<DeviceStatusResponse> newlyAdded = new ArrayList<>();

		// 4. 신규 디바이스 조회 과정
		for (SmartThingsDevice dto : response.getDevices()) {
			String controlId = dto.getDeviceId();
			String model = dto.getDeviceModel();

			// 4-1. 이미 등록된 controlId 이면 스킵
			if (existingControlIds.contains(controlId)) continue;

			// 4-2. deviceModel에 "smartTag" (대소문자 무관) 포함되면 스킵
			//		smartTag 는 UWB 활용을 위해 등록한 것이기 때문에 관리대상 디바이스가 아님
			if (model != null && model.toLowerCase().contains("smarttag")) continue;

			// 4-2. 신규 디바이스 저장 : PostgreSQL
			
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
					.deviceModel(dto.getDeviceModel())
					.deviceType(inferredType.name())
					.build();
			deviceRepository.save(device);

			// 4-3. 응답용 DTO 생성 : 새롭게 추가된 디바이스 정보만 넘겨줌
			newlyAdded.add(DeviceStatusResponse.builder()
					.tagNumber(null)
					.deviceId(device.getDeviceId())
					.installedAppId(installedAppId)
					.deviceName(device.getDeviceName())
					.activated(false)
					.build());
		}

		return newlyAdded;
	} // ...getSmartThingsDevices()


	// [ DB에 저장된 사용자의 기기 목록 불러오기 ]
	public List<DeviceStatusResponse> getAllDeviceByMember() {
		// 인증 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long memberId = Long.valueOf(authentication.getName());

		List<Device> devices = deviceRepository.findByMemberId(memberId);
		return devices.stream()
				.map(device -> DeviceStatusResponse.builder()
						.tagNumber(device.getTagNumber())
						.deviceId(device.getDeviceId())
						.installedAppId(device.getInstalledAppId())
						.deviceImg(device.getDeviceUrl())
						.deviceName(device.getDeviceName())
						.deviceType(device.getDeviceType())
						.activated(extractActivatedFromControl(device.getControl()))
						.build())
				.collect(Collectors.toList());
	}

	// control JSON에서 activated 값 추출 (예: {"activated": true})
	private Boolean extractActivatedFromControl(Map<String, Object> control) {
		if (control == null) return false;
		Object value = control.get("activated");
		return value instanceof Boolean ? (Boolean) value : false;
	}


	// UWB : TagNumber 로 기기 정보 찾기
	public Object getDeviceStatusByTagNumber(int tagNumber, Long memberId) {
		// 1. TagNumber 로 Device 정보 찾기
		Device device = deviceRepository.findByTagNumberAndMemberId(tagNumber, memberId)
				.orElseThrow(() -> new EntityNotFoundException("해당 태그 번호의 디바이스가 없습니다."));

		DeviceType inferredType = DeviceType.fromModelName(device.getDeviceModel());
		return switch (inferredType) {
			case SWITCH -> switchService.getSwitchStatus(device.getDeviceId(), memberId);
			case AUDIO -> audioService.getAudioStatus(device.getDeviceId(), memberId);
			// case LIGHT -> lightService.getLightStatus(...);
			default -> throw new IllegalArgumentException("지원하지 않는 디바이스 타입입니다: " + inferredType);
		};
	}


}
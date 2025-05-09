package com.hogumiwarts.lumos.device.service;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.entity.DeviceType;
import com.hogumiwarts.lumos.device.service.support.DeviceStatusResolver;
import com.hogumiwarts.lumos.device.util.AuthUtil;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceStatusResolver deviceStatusResolver;

    private final ExternalDeviceService externalDeviceService;

    private final SwitchService switchService;
    private final AudioService audioService;
    private final LightService lightService;

	// [ SmartThingsApp 에 등록된 모든 기기 정보 불러오기 (node.js 통신) ]
	public List<DeviceStatusResponse> getSmartThingsDevices(String installedAppId) {
		// 0. memberId 를 기준으로 smartThings API 호출에 필요한 installedAppId 찾기
		Long memberId = AuthUtil.getMemberId();

        System.out.println("인증키 : " + installedAppId);

        // 1. SmartThings API 호출
        DeviceListResponse response = externalDeviceService.fetchDeviceList(installedAppId);

		// 2. DB에서 DeviceId 추출 (새로 검색된 디바이스, 이미 등록되어있는 디바이스 비교를 위해)
		Set<String> existingControlIds = deviceRepository.findByMemberId(memberId)
				.orElse(new ArrayList<>())
				.stream()
				.map(Device::getControlId)
				.collect(Collectors.toSet());

        System.out.println("요청값 : " + response);

        // 3. 신규 디바이스 조회
        List<DeviceStatusResponse> newlyAdded = new ArrayList<>();
        for (SmartThingsDevice dto : response.getDevices()) {
            String controlId = dto.getDeviceId();
            String model = dto.getDeviceModel();

            // 3-1. 이미 등록된 controlId 이면 스킵
            if (existingControlIds.contains(controlId)) continue;

            // 3-2. deviceModel에 "smartTag" (대소문자 무관) 포함되면 스킵
            //		smartTag 는 UWB 활용을 위해 등록한 것이기 때문에 관리대상 디바이스가 아님
            if (model != null) {
                String lowerModel = model.toLowerCase();
                if (lowerModel.contains("smarttag") || lowerModel.contains("hub") || model.contains("허브")) continue;
            }

            // 3-3. 신규 디바이스 저장 : PostgreSQL
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
					boolean activated = deviceStatusResolver.getActivatedStatus(device); // ⭐️ 이게 핵심
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

//		return devices.stream()
//				.map(device -> DeviceStatusResponse.builder()
//						.tagNumber(device.getTagNumber())
//						.deviceId(device.getDeviceId())
//						.installedAppId(device.getInstalledAppId())
//						.deviceImg(device.getDeviceUrl())
//						.deviceName(device.getDeviceName())
//						.deviceType(device.getDeviceType())
//						.activated(extractActivatedFromControl(device.getControl()))
//						.build())
//				.collect(Collectors.toList());
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
            default -> throw new IllegalArgumentException("지원하지 않는 디바이스 타입입니다: " + inferredType);
        };
    }


}
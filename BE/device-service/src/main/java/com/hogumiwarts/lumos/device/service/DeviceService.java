package com.hogumiwarts.lumos.device.service;

import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

	private final DeviceRepository deviceRepository;
	private final ExternalDeviceService externalDeviceService;

	// [ SmartThingsApp 에 등록된 모든 기기 정보 불러오기 (node.js 통신) ]
	public List<NewDiscoveredDeviceResponse> getSmartThingsDevices(Long memberId, String installedAppId){
		// 1-1. SmartThings 에서 디바이스 목록 불러오기
		DeviceListResponse response = externalDeviceService.fetchDeviceList(installedAppId);

		// 1-2. 현재 DB에 저장된 MemberID 의 디바이스 목록 불러오기 (기존 디바이스와 중복체크)
		List<Device> savedDevices = deviceRepository.findByMemberId(memberId);
		Set<String> existingControlIds = savedDevices.stream()
				.map(Device::getControlId)
				.collect(Collectors.toSet());

		// 1-3. 새로 발견된 기기만 DB에 저장하고, Client 에게 돌려줄 Response 생성
		List<NewDiscoveredDeviceResponse> newlyAdded = new ArrayList<>();

		for (SmartThingsDevice dto : response.getDevices()) {
			String controlId = dto.getDeviceId();

			if (!existingControlIds.contains(controlId)) {
				Device device = Device.builder()
						.deviceId(null) // Auto-generated
						.controlId(controlId)
						.deviceName(dto.getLabel())
						.deviceUrl(null) // 또는 default image
						.installedAppId(installedAppId)
						.memberId(memberId)
						.build();
				deviceRepository.save(device);

				newlyAdded.add(NewDiscoveredDeviceResponse.builder()
						.controlId(controlId)
						.deviceName(dto.getLabel())
						.deviceModel(dto.getDeviceModel())
						.deviceImg(null) // 임시 비워둠
						.build());
			}
		}

		return newlyAdded;
	} // ...getSmartThingsDevices()


	// [ DB에 저장된 사용자의 기기 목록 불러오기 ]
	public List<DeviceResponse> getAllDeviceByMember(Long memberId) {
		List<Device> devices = deviceRepository.findByMemberId(memberId);
		return devices.stream()
				.map(device -> DeviceResponse.builder()
						.tagNumber(device.getTagNumber())
						.deviceId(device.getDeviceId())
						.installedAppId(device.getInstalledAppId())
						.deviceImg(device.getDeviceUrl())
						.deviceName(device.getDeviceName())
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


	// UWB : TagNumber 로 기기 정보 찾기 : 그냥 내부 DB만 조회하던 버전 (node.js 서버와 연결필요)
	public DeviceStatusResponse getDeviceStatusByTagNumber(int tagNumber, Long memberId) {
		Device device = deviceRepository.findByTagNumberAndMemberId(tagNumber, memberId)
				.orElseThrow(() -> new EntityNotFoundException("해당 태그 번호의 디바이스가 없습니다."));

		return DeviceStatusResponse.builder()
				.tagNumber(device.getTagNumber())
				.deviceId(device.getDeviceId())
				.deviceImg("http://example.com/image3.jpg") // TODO: 나중에 실제 이미지 필드로 교체
				.deviceName(device.getDeviceName())
				.activated(
						device.getControl() != null &&
								device.getControl().containsKey("power") &&
								"on".equalsIgnoreCase(String.valueOf(device.getControl().get("power")))
				)
				.build();
	}


	// node.js API 제어 명령어 호출
	public DeviceStatusResponse executeCommand(Long deviceId, PowerControlRequest request) {
		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(() -> new IllegalArgumentException("디바이스를 찾을 수 없습니다."));
		return externalDeviceService.executeCommand(deviceId, request, DeviceStatusResponse.class);
	}

}
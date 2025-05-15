package com.hogumiwarts.lumos.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.dto.audio.AudioDetailResponse;
import com.hogumiwarts.lumos.device.dto.audio.VolumeControlRequest;
import com.hogumiwarts.lumos.device.dto.device.DeviceStatusResponse;
import com.hogumiwarts.lumos.device.entity.Device;
import com.hogumiwarts.lumos.device.repository.DeviceRepository;
import com.hogumiwarts.lumos.device.util.DeviceCommandUtil;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AudioService {

	private final DeviceRepository deviceRepository;
	private final ExternalDeviceService externalDeviceService;

	public AudioDetailResponse getAudioStatus(Long deviceId) {
		Long memberId = AuthUtil.getMemberId();

		Device device = (Device) deviceRepository.findByDeviceIdAndMemberId(deviceId, memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "deviceId에 해당하는 디바이스를 찾을 수 없습니다."));

		JsonNode raw = externalDeviceService.fetchDeviceStatus(deviceId);
		JsonNode main = raw.path("status").path("components").path("main");

		// Playback 상태 파싱
		String playbackValue = main
				.path("mediaPlayback")
				.path("playbackStatus")
				.path("value")
				.asText(null);

		boolean activated = switch (playbackValue != null ? playbackValue.toLowerCase() : "") {
			case "playing", "fast forwarding", "rewinding" -> true;
			default -> false;
		};

		// Play 정보
		JsonNode trackDataNode = main.path("audioTrackData").path("audioTrackData").path("value");
		String albumArtUrl = trackDataNode.path("albumArtUrl").asText(null);
		String artist = trackDataNode.path("artist").asText(null);
		String albumTitle = trackDataNode.path("title").asText(null);

		// 미디어 볼륨
		Integer volume = null;
		JsonNode groupVolNode = main.path("mediaGroup").path("groupVolume").path("value");
		if (!groupVolNode.isMissingNode() && groupVolNode.isInt()) {
			volume = groupVolNode.asInt();
		} else {
			// fallback to audioVolume
			JsonNode volNode = main.path("audioVolume").path("volume").path("value");
			if (!volNode.isMissingNode() && volNode.isInt()) {
				volume = volNode.asInt();
			}
		}


		return AudioDetailResponse.builder()
				.tagNumber(device.getTagNumber())
				.deviceId(device.getDeviceId())
				.deviceImg(device.getDeviceUrl())
				.deviceName(device.getDeviceName())
				.manufacturerCode(device.getDeviceManufacturer())
				.deviceModel(device.getDeviceModel())
				.deviceType(device.getDeviceType())
				.activated(activated)
				.deviceImg(device.getDeviceUrl())
				.audioImg(albumArtUrl)
				.audioName(albumTitle)
				.audioArtist(artist)
				.audioVolume(volume)
				.build();
	}

	// buildAudioVolumeCommand
	public void updateAudioVolume(Long deviceId, VolumeControlRequest request) {
		CommandRequest command = DeviceCommandUtil.buildAudioVolumeCommand(request.getVolume());
		externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
	}

	public void updateAudioPlayback(Long deviceId, PowerControlRequest request) {
		CommandRequest command = DeviceCommandUtil.buildAudioPlayBackCommand(request.getActivated());
		externalDeviceService.executeCommand(deviceId, command, DeviceStatusResponse.class);
	}

}
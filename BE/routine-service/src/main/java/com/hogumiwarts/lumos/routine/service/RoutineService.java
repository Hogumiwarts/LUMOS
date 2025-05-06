package com.hogumiwarts.lumos.routine.service;

import com.hogumiwarts.lumos.routine.client.GestureServiceClient;
import com.hogumiwarts.lumos.routine.dto.DeviceDto;
import com.hogumiwarts.lumos.routine.dto.GestureInfo;
import com.hogumiwarts.lumos.routine.dto.RoutineResponse;
import com.hogumiwarts.lumos.routine.entity.Device;
import com.hogumiwarts.lumos.routine.entity.Routine;
import com.hogumiwarts.lumos.routine.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final GestureServiceClient gestureServiceClient;

    // 루틴 정보 불러오기
    public RoutineResponse getRoutines(Long memberId) {

        Routine routine = routineRepository.findByMemberId(memberId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("루틴이 없습니다."));

        // 루틴 내부의 control  조회
        List<Map<String, Object>> devices = routine.getControl();

        // gesture-service에서 제스처 정보 조회
        GestureInfo gesture = gestureServiceClient.getGestureInfo(routine.getMemberGestureId(), memberId);

        // 응답 생성
        return RoutineResponse.builder()
                .gestureName(gesture.getGestureName())
                .gestureImg(gesture.getImageUrl())
                .devices(devices.stream()
                        .map(this::mapToDeviceDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private DeviceDto mapToDeviceDto(Map<String, Object> deviceMap) {
        Long deviceId = ((Number) deviceMap.get("deviceId")).longValue();
        List<Map<String, Object>> controlList = List.of((Map<String, Object>) deviceMap.get("control")); // 단일 control을 리스트로 감쌈

        return DeviceDto.builder()
                .deviceId(deviceId)
                .deviceName(null)
                .deviceImg(null)
                .control(controlList)
                .build();
    }


}

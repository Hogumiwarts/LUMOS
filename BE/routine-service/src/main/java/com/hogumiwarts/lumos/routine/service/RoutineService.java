package com.hogumiwarts.lumos.routine.service;

import com.hogumiwarts.lumos.routine.client.DeviceServiceClient;
import com.hogumiwarts.lumos.routine.client.GestureServiceClient;
import com.hogumiwarts.lumos.routine.dto.*;
import com.hogumiwarts.lumos.routine.entity.Routine;
import com.hogumiwarts.lumos.routine.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final GestureServiceClient gestureServiceClient;
    private final DeviceServiceClient deviceServiceClient;

    // 루틴 생성
    public SuccessResponse createRoutine(Long memberId, RoutineCreateRequest request) {

        try {
            Routine routine = Routine.builder()
                    .memberId(memberId)
                    .routineName(request.getRoutineName())
                    .routineIcon(request.getRoutineIcon())
                    .memberGestureId(request.getGestureId())
                    .control(request.getDevices().stream()
                            .map(device -> Map.of(
                                    "deviceId", device.getDeviceId(),
                                    "deviceName", device.getDeviceName(),
                                    "deviceImg", device.getDeviceImg(),
                                    "control", device.getControl()
                            ))
                            .collect(Collectors.toList()))
                    .build();

            routineRepository.save(routine);
            return SuccessResponse.of(true);
        } catch (Exception e) {
            // 로깅 가능
            return SuccessResponse.of(false);
        }
    }

    // 루틴 목록 조회
    public List<RoutineResponse> getRoutineList(Long memberId) {
        List<Routine> routines = routineRepository.findByMemberId(memberId);

        return routines.stream()
                .map(routine -> {
                    // 제스처 이름 가져오기
                    GestureInfo gesture = gestureServiceClient.getGestureInfo(
                            routine.getMemberGestureId(),
                            memberId
                    );

                    return RoutineResponse.builder()
                            .routineId(routine.getRoutineId())
                            .routineName(routine.getRoutineName()) // routineName 필드가 없다면 routine.getTitle() 등으로 교체
                            .routineIcon(routine.getRoutineIcon())
                            .gestureName(gesture.getGestureName())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 루틴 수정
    public SuccessResponse updateRoutine(Long routineId, Long memberId, RoutineUpdateRequest request) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("루틴을 찾을 수 없습니다."));

        if (!routine.getMemberId().equals(memberId)) {
            throw new RuntimeException("해당 멤버의 루틴이 아닙니다.");
        }

        request.getRoutineName().ifPresent(routine::setRoutineName);
        request.getRoutineIcon().ifPresent(routine::setRoutineIcon);
        request.getGestureId().ifPresent(routine::setMemberGestureId);
        request.getDevices().ifPresent(routine::setControl);

        routine.setUpdatedAt(LocalDateTime.now());
        routineRepository.save(routine);
        return SuccessResponse.of(true);
    }


    // 루틴 삭제
    public SuccessResponse deleteRoutine(Long routineId, Long memberId) {

        try {
            Optional<Routine> routine = routineRepository.findByRoutineIdAndMemberId(routineId, memberId);

            if (routine.isEmpty()) {
                return SuccessResponse.of(false); // 없는 경우 false 응답
            }

            routineRepository.delete(routine.get());
            return SuccessResponse.of(true);

        } catch (Exception e) {
            return SuccessResponse.of(false); // 예외 발생 시 false
        }
    }


    // TODO: 진행중
    // 루틴별 기기 정보 불러오기
    public RoutineDevicesResponse getRoutineDevices(Long memberId, Long routineId) {

        Optional<Routine> optionalRoutine = routineRepository.findByMemberIdAndRoutineId(memberId, routineId)
                .stream()
                .findFirst();

        if (optionalRoutine.isEmpty()) {
            return RoutineDevicesResponse.builder()
                    .gestureName(null)
                    .gestureImg(null)
                    .devices(List.of()) // ✅ 빈 리스트
                    .build();
        }

        Routine routine = optionalRoutine.get();


        // 루틴 내부의 control  조회
        List<Map<String, Object>> devices = routine.getControl();

        // gesture-service에서 제스처 정보 조회
        GestureInfo gesture = gestureServiceClient.getGestureInfo(routine.getMemberGestureId(), memberId);

        List<DeviceResponse> allDevices = deviceServiceClient.getAllDeviceByMember(memberId).getData();

        // 🔽 Map 형태로 캐싱
        Map<Long, DeviceResponse> deviceMap = allDevices.stream()
                .collect(Collectors.toMap(DeviceResponse::getDeviceId, d -> d));

        // 응답 생성
        return RoutineDevicesResponse.builder()
                .gestureName(gesture.getGestureName())
                .gestureImg(gesture.getGestureImg())
                .devices(devices.stream()
                        .map(control -> mapToDeviceDto(control, deviceMap))
                        .collect(Collectors.toList()))
                .build();
    }

    private DeviceDto mapToDeviceDto(Map<String, Object> deviceMapRaw, Map<Long, DeviceResponse> deviceDetailsMap) {
        Long deviceId = ((Number) deviceMapRaw.get("deviceId")).longValue();
        List<Map<String, Object>> controlList = List.of((Map<String, Object>) deviceMapRaw.get("control"));

        DeviceResponse detail = deviceDetailsMap.get(deviceId);

        return DeviceDto.builder()
                .deviceId(deviceId)
                .deviceName(detail != null ? detail.getDeviceName() : null)
                .deviceImg(detail != null ? detail.getDeviceImg() : null)
                .control(controlList)
                .build();
    }


}

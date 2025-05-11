package com.hogumiwarts.lumos.routine.service;

import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.routine.client.DeviceServiceClient;
import com.hogumiwarts.lumos.routine.client.GestureServiceClient;
import com.hogumiwarts.lumos.routine.client.SmartThingsServiceClient;
import com.hogumiwarts.lumos.routine.dto.*;
import com.hogumiwarts.lumos.routine.entity.Routine;
import com.hogumiwarts.lumos.routine.repository.RoutineRepository;
import com.hogumiwarts.lumos.util.AuthUtil;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final GestureServiceClient gestureServiceClient;
    private final DeviceServiceClient deviceServiceClient;
    private final SmartThingsServiceClient smartThingsClient;

    // 루틴 생성
    @Transactional
    public RoutineCreateResponse createRoutine(RoutineCreateRequest request) {
        Long memberId = AuthUtil.getMemberId();
        Optional<Long> gestureIdOpt = request.getGestureId();

        if (gestureIdOpt.isPresent()) {
            Long gestureId = gestureIdOpt.get();

            // 유효한 제스처인지 확인
            if (gestureServiceClient.getGesture(gestureId).getData() == null) {
                throw new CustomException(ErrorCode.GESTURE_NOT_FOUND);
            }

            // 기존 루틴에서 동일한 (memberId, gestureId) 조합이 있는지 확인 후 gesture 연결 끊기
            routineRepository.clearGestureBinding(memberId, gestureId);
        }

        // 새로운 루틴 생성
        Routine routine = Routine.builder()
            .memberId(memberId)
            .gestureId(gestureIdOpt.orElse(null))
            .routineName(request.getRoutineName())
            .routineIcon(request.getRoutineIcon())
            .devices(request.getDevices())
            .build();

        routineRepository.save(routine);

        return RoutineCreateResponse.from(routine);
    }

    // 루틴 목록 조회
    public List<RoutineListResponse> getRoutineList() {
        Long memberId = AuthUtil.getMemberId();

        List<Routine> routines = routineRepository.findByMemberIdOrderByRoutineIdAsc(memberId);

        return routines.stream()
                .map(routine -> {
                    // 제스처 이름 가져오기
                    GestureResponse gesture = routine.getGestureId() == null ? null : gestureServiceClient.getGesture(routine.getGestureId()).getData();

                    return RoutineListResponse.builder()
                            .routineId(routine.getRoutineId())
                            .routineName(routine.getRoutineName()) // routineName 필드가 없다면 routine.getTitle() 등으로 교체
                            .routineIcon(routine.getRoutineIcon())
                            .gestureName(gesture == null ? null : gesture.getGestureName())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 루틴 상세 조회
    @Transactional
    public RoutineDetailResponse getRoutine(Long routineId) {
        // 1. 루틴 조회
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));

        Long gestureId = routine.getGestureId();

        // 2. 제스처 정보 조회 (gesture-service)
        GestureResponse gesture = gestureId == null ? null : gestureServiceClient.getGesture(gestureId).getData();

        // 3. control 안의 deviceId 목록 추출
        List<Long> deviceIds = routine.getDevices().stream()
            .map(DevicesCreateRequest::getDeviceId)
            .toList();

        // 4. device-service에서 상세 정보 조회
        List<DevicesResponse> deviceDetails = deviceServiceClient.getDeviceDetailsByIds(deviceIds);

        // 5. 디바이스 정보 매핑
        Map<Long, DevicesResponse> deviceDetailMap = deviceDetails.stream()
            .collect(Collectors.toMap(DevicesResponse::getDeviceId, d -> d));

        List<DevicesResponse> devices = routine.getDevices().stream()
            .map(device -> {
                DevicesResponse detail = deviceDetailMap.get(device.getDeviceId());

                return DevicesResponse.builder()
                    .deviceId(device.getDeviceId())
                    .deviceName(detail != null ? detail.getDeviceName() : null)
                    .deviceType(detail != null ? detail.getDeviceType() : null)
                    .deviceImageUrl(detail != null ? detail.getDeviceImageUrl() : null)
                    .commands(device.getCommands())
                    .build();
            })
            .toList();

        // 6. 최종 DTO 조립
        return RoutineDetailResponse.from(routine, gesture, devices);
    }

    // 루틴 수정
    public RoutineCreateResponse updateRoutine(Long routineId, RoutineUpdateRequest request) {
        Long memberId = AuthUtil.getMemberId();

        // 유효한 제스처인지 확인
        Optional<Long> gestureId = request.getGestureId();

        if (gestureId.isPresent()) {
            if (gestureServiceClient.getGesture(gestureId.get()).getData() == null) {
                throw new CustomException(ErrorCode.GESTURE_NOT_FOUND);
            }

            // 기존에 해달 제스처를 사용하는 루틴이 있는지 확인
            Optional<Routine> existing = routineRepository.findByMemberIdAndGestureId(memberId, gestureId.get());

            if (existing.isPresent()) {
                // 기존 루틴 업데이트
                Routine routine = existing.get();
                routine.setGestureId(null);
            }
        }

        // 루틴 조회
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));

        gestureId.ifPresent(routine::setGestureId);
        request.getRoutineName().ifPresent(routine::setRoutineName);
        request.getRoutineIcon().ifPresent(routine::setRoutineIcon);
        request.getDevices().ifPresent(routine::setDevices);
        routineRepository.save(routine);

        return RoutineCreateResponse.from(routine);
    }

    // 루틴 삭제
    public void deleteRoutine(Long routineId) {
        Long memberId = AuthUtil.getMemberId();

        Routine routine = routineRepository.findByRoutineIdAndMemberId(routineId, memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));
        routineRepository.delete(routine);
    }

    // 루틴 실행
    public void executeRoutineByGestureId(Long gestureId) {
        Long memberId = AuthUtil.getMemberId();

        Routine routine = routineRepository.findByMemberIdAndGestureId(memberId, gestureId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_GESTURE_NOT_FOUND));

        List<Long> failedDeviceIds = new ArrayList<>();

        routine.getDevices().forEach(device -> {
            try {
                smartThingsClient.executeCommand(
                    device.getControlId(),
                    device.getInstalledAppId(),
                    new CommandExecuteRequest(device.getCommands())
                );
            } catch (Exception e) {
                failedDeviceIds.add(device.getDeviceId());
                log.error("스마트싱스 제어 실패: deviceId={}, error={}", device.getDeviceId(), e.getMessage());
            }
        });

        if (!failedDeviceIds.isEmpty()) {
            throw new CustomException(ErrorCode.ROUTINE_PARTIAL_FAILURE);
        }
    }

    @Transactional
    public RoutineResponse getRoutineByMemberIdAndGestureId(Long memberId, Long gestureId) {
        Routine routine = routineRepository.findByMemberIdAndGestureId(memberId, gestureId).orElse(null);

        if (routine == null) {
            return null;
        }

        return RoutineResponse.builder()
            .routineId(routine.getRoutineId())
            .routineName(routine.getRoutineName())
            .build();
    }
}
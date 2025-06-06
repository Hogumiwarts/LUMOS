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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        // 제스처 ID 유효성 검사 및 기존 루틴 연결 해제
        request.getGestureId().ifPresent(gestureId -> {
            if (gestureServiceClient.getGesture(gestureId).getData() == null) {
                throw new CustomException(ErrorCode.GESTURE_NOT_FOUND);
            }
            routineRepository.clearGestureBinding(memberId, gestureId);
        });

        // 디바이스 ID만 추출
        List<Long> deviceIds = request.getDevices().stream()
            .map(DevicesCreateRequest::getDeviceId)
            .toList();

        // 디바이스 상세 정보 조회
        List<DevicesResponse> deviceDetails = deviceServiceClient.getDeviceDetailsByIds(deviceIds);

        // List → Map 변환
        Map<Long, DevicesResponse> deviceDetailMap = deviceDetails.stream()
            .collect(Collectors.toMap(DevicesResponse::getDeviceId, d -> d));

        // 존재하지 않는 디바이스 ID 확인
        List<Long> notFoundDeviceIds = deviceIds.stream()
            .filter(id -> !deviceDetailMap.containsKey(id))
            .toList();

        if (!notFoundDeviceIds.isEmpty()) {
            log.warn("❌ 존재하지 않는 디바이스 ID: {}", notFoundDeviceIds);
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND, "존재하지 않는 디바이스 ID: " + notFoundDeviceIds);
        }

        // 루틴 생성
        Routine routine = Routine.builder()
            .memberId(memberId)
            .gestureId(request.getGestureId().orElse(null))
            .routineName(request.getRoutineName())
            .routineIcon(request.getRoutineIcon())
            .devices(request.getDevices())
            .build();

        routineRepository.save(routine);

        List<DevicesCreateResponse> deviceResponses = routine.getDevices().stream()
            .map(d -> DevicesCreateResponse.builder()
                .deviceId(d.getDeviceId())
                .commands(d.getCommands())
                .deviceName(deviceDetailMap.get(d.getDeviceId()).getDeviceName())
                .deviceType(deviceDetailMap.get(d.getDeviceId()).getDeviceType())
                .deviceImageUrl(deviceDetailMap.get(d.getDeviceId()).getDeviceImageUrl())
                .build())
            .toList();

        return RoutineCreateResponse.from(routine, deviceResponses);
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

        List<DevicesCreateResponse> devices = routine.getDevices().stream()
            .map(device -> {
                DevicesResponse detail = deviceDetailMap.get(device.getDeviceId());

                return DevicesCreateResponse.builder()
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
    @Transactional
    public RoutineCreateResponse updateRoutine(Long routineId, RoutineUpdateRequest request) {
        Long memberId = AuthUtil.getMemberId();

        // 1. 루틴 조회 및 소유자 검증
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));

        if (!routine.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 2. 제스처 처리
        request.getGestureId().ifPresent(gestureId -> {
            if (gestureServiceClient.getGesture(gestureId).getData() == null) {
                throw new CustomException(ErrorCode.GESTURE_NOT_FOUND);
            }

            // 중복 gesture 해제 (현재 루틴 제외하고)
            routineRepository.clearGestureBindingExcludeCurrent(memberId, gestureId, routineId);

            routine.setGestureId(gestureId);
        });

        // 3. 루틴명, 아이콘 수정
        request.getRoutineName().ifPresent(routine::setRoutineName);
        request.getRoutineIcon().ifPresent(routine::setRoutineIcon);

        // 4. 디바이스 정보 병합
        List<DevicesResponse> deviceDetails = Collections.emptyList(); // default empty
        if (request.getDevices().isPresent()) {
            List<DevicesCreateRequest> deviceRequest = request.getDevices().get();

            List<Long> deviceIds = deviceRequest.stream()
                .map(DevicesCreateRequest::getDeviceId)
                .toList();

            deviceDetails = deviceServiceClient.getDeviceDetailsByIds(deviceIds);

            Map<Long, DevicesResponse> deviceDetailMap = deviceDetails.stream()
                .collect(Collectors.toMap(DevicesResponse::getDeviceId, d -> d));

            List<DevicesCreateRequest> mergedDevices = deviceRequest.stream()
                .map(req -> {
                    DevicesResponse match = deviceDetailMap.get(req.getDeviceId());
                    if (match == null) {
                        throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
                    }

                    return DevicesCreateRequest.builder()
                        .deviceId(req.getDeviceId())
                        .commands(req.getCommands())
                        .build();
                })
                .toList();

            routine.setDevices(mergedDevices);
        }

        routineRepository.save(routine);

        // 5. 응답 변환
        Map<Long, DevicesResponse> detailMap = deviceDetails.stream()
            .collect(Collectors.toMap(DevicesResponse::getDeviceId, d -> d));

        List<DevicesCreateResponse> responseDevices = routine.getDevices().stream()
            .map(d -> {
                DevicesResponse detail = detailMap.get(d.getDeviceId());
                return DevicesCreateResponse.builder()
                    .deviceId(d.getDeviceId())
                    .commands(d.getCommands())
                    .deviceName(detail != null ? detail.getDeviceName() : null)
                    .deviceType(detail != null ? detail.getDeviceType() : null)
                    .deviceImageUrl(detail != null ? detail.getDeviceImageUrl() : null)
                    .build();
            })
            .toList();

        return RoutineCreateResponse.from(routine, responseDevices);
    }

    // 루틴 삭제
    public void deleteRoutine(Long routineId) {
        Long memberId = AuthUtil.getMemberId();

        Routine routine = routineRepository.findByRoutineIdAndMemberId(routineId, memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));
        routineRepository.delete(routine);
    }

    // 제스처로 루틴 실행
    @Transactional
    public void executeRoutineByGestureId(Long gestureId) {
        Long memberId = AuthUtil.getMemberId();

        // 1. 루틴 조회
        Routine routine = routineRepository.findByMemberIdAndGestureId(memberId, gestureId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_GESTURE_NOT_FOUND));

        // 2. deviceId 리스트 추출
        List<Long> deviceIds = routine.getDevices().stream()
            .map(DevicesCreateRequest::getDeviceId)
            .toList();

        // 3. 디바이스 정보 조회
        List<DevicesResponse> devicesInfo = deviceServiceClient.getDeviceDetailsByIds(deviceIds);

        // 4. deviceId → DevicesResponse 맵핑
        Map<Long, DevicesResponse> deviceMap = devicesInfo.stream()
            .collect(Collectors.toMap(DevicesResponse::getDeviceId, d -> d));

        // 5. 실패 디바이스 리스트
        List<Long> failedDeviceIds = new ArrayList<>();

        // 6. 디바이스별 명령 실행
        for (DevicesCreateRequest device : routine.getDevices()) {
            try {
                DevicesResponse deviceInfo = deviceMap.get(device.getDeviceId());

                if (deviceInfo == null) {
                    log.error("💥 디바이스 정보 누락: deviceId={}", device.getDeviceId());
                    failedDeviceIds.add(device.getDeviceId());
                    continue;
                }

                CommandExecuteRequest commandRequest = new CommandExecuteRequest(device.getCommands());

                smartThingsClient.executeCommand(
                    deviceInfo.getControlId(),
                    deviceInfo.getInstalledAppId(),
                    commandRequest
                );

            } catch (Exception e) {
                failedDeviceIds.add(device.getDeviceId());
                log.error("💥 스마트싱스 제어 실패: deviceId={}, error={}", device.getDeviceId(), e.getMessage());
            }
        }

        // 7. 일부 실패 시 예외 던짐
        if (!failedDeviceIds.isEmpty()) {
            log.warn("⚠️ 일부 디바이스 제어 실패: {}", failedDeviceIds);
            throw new CustomException(ErrorCode.ROUTINE_PARTIAL_FAILURE);
        }
    }

    // 버튼으로 루틴 실행
    public void executeRoutineById(Long routineId) {
        // 1. 루틴 조회
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new CustomException(ErrorCode.ROUTINE_NOT_FOUND));

        // 2. deviceId 리스트 추출
        List<Long> deviceIds = routine.getDevices().stream()
            .map(DevicesCreateRequest::getDeviceId)
            .toList();

        // 3. 디바이스 정보 조회
        List<DevicesResponse> devicesInfo = deviceServiceClient.getDeviceDetailsByIds(deviceIds);

        // 4. deviceId → DevicesResponse 맵핑
        Map<Long, DevicesResponse> deviceMap = devicesInfo.stream()
            .collect(Collectors.toMap(DevicesResponse::getDeviceId, d -> d));

        // 5. 실패 디바이스 리스트
        List<Long> failedDeviceIds = new ArrayList<>();

        // 6. 디바이스별 명령 실행
        for (DevicesCreateRequest device : routine.getDevices()) {
            try {
                DevicesResponse deviceInfo = deviceMap.get(device.getDeviceId());

                if (deviceInfo == null) {
                    log.error("💥 디바이스 정보 누락: deviceId={}", device.getDeviceId());
                    failedDeviceIds.add(device.getDeviceId());
                    continue;
                }

                CommandExecuteRequest commandRequest = new CommandExecuteRequest(device.getCommands());

                smartThingsClient.executeCommand(
                    deviceInfo.getControlId(),
                    deviceInfo.getInstalledAppId(),
                    commandRequest
                );

            } catch (Exception e) {
                failedDeviceIds.add(device.getDeviceId());
                log.error("💥 스마트싱스 제어 실패: deviceId={}, error={}", device.getDeviceId(), e.getMessage());
            }
        }

        // 7. 일부 실패 시 예외 던짐
        if (!failedDeviceIds.isEmpty()) {
            log.warn("⚠️ 일부 디바이스 제어 실패: {}", failedDeviceIds);
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
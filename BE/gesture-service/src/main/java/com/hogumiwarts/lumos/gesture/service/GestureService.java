package com.hogumiwarts.lumos.gesture.service;

import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.gesture.client.RoutineServiceClient;
import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import com.hogumiwarts.lumos.gesture.dto.GestureWithRoutineResponse;
import com.hogumiwarts.lumos.gesture.dto.RoutineResponse;
import com.hogumiwarts.lumos.gesture.entity.Gesture;
import com.hogumiwarts.lumos.gesture.repository.GestureRepository;
import com.hogumiwarts.lumos.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestureService {

    private final GestureRepository gestureRepository;
    private final RoutineServiceClient routineServiceClient;

    // 전체 제스처 목록 조회
    public List<GestureWithRoutineResponse> getGestures() {
        Long memberId = AuthUtil.getMemberId();
        List<Gesture> gestures = gestureRepository.findAll();

        return gestures.stream().map(gesture -> {
            RoutineResponse routine = routineServiceClient.getRoutineByGesture(memberId, gesture.getGestureId());

            return GestureWithRoutineResponse.builder()
                .gestureId(gesture.getGestureId())
                .gestureName(gesture.getGestureName())
                .gestureImageUrl(gesture.getImageUrl())
                .gestureDescription(gesture.getDescription())
                .routineId(routine != null ? routine.getRoutineId() : null)
                .routineName(routine != null ? routine.getRoutineName() : null)
                .build();
        }).collect(Collectors.toList());
    }

    // 제스처 상세 정보 조회
    @Transactional(readOnly = true)
    public GestureResponse getGesture(Long gestureId) {
        Gesture gesture = gestureRepository.findById(gestureId).orElse(null);

        if (gesture == null) {
            return null;
        }

        return GestureResponse.builder()
            .gestureId(gesture.getGestureId())
            .gestureName(gesture.getGestureName())
            .gestureImageUrl(gesture.getImageUrl())
            .gestureDescription(gesture.getDescription())
            .build();
    }
}

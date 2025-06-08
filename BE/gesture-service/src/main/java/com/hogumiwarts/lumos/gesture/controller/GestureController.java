package com.hogumiwarts.lumos.gesture.controller;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.gesture.docs.GestureApiSpec;
import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import com.hogumiwarts.lumos.gesture.dto.GestureWithRoutineResponse;
import com.hogumiwarts.lumos.gesture.service.GestureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gesture")
@RequiredArgsConstructor
@Slf4j
public class GestureController implements GestureApiSpec {

    private final GestureService gestureService;

    // 모든 제스처 조회
    @GetMapping
    public ResponseEntity<CommonResponse<List<GestureWithRoutineResponse>>> getGestures() {
        List<GestureWithRoutineResponse> gestures = gestureService.getGestures();
        return ResponseEntity.ok(CommonResponse.ok(gestures));
    }

    // gestureId에 해당하는 제스처 상세 정보 조회
    @GetMapping("/{gestureId}")
    public ResponseEntity<CommonResponse<GestureResponse>> getGesture(@PathVariable("gestureId") Long gestureId) {
        GestureResponse response = gestureService.getGesture(gestureId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}

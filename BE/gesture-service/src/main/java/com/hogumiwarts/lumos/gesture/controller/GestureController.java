package com.hogumiwarts.lumos.gesture.controller;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.gesture.docs.GestureApiSpec;
import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import com.hogumiwarts.lumos.gesture.service.GestureService;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public ResponseEntity<CommonResponse<List<GestureResponse>>> getGestures(@RequestParam Long memberId) {
        List<GestureResponse> gestures = gestureService.getGestures(memberId);
        return ResponseEntity.ok(CommonResponse.ok(gestures));
    }

    // gestureId에 해당하는 제스처 상세 정보 조회
    @GetMapping("/{memberGestureId}")
    public ResponseEntity<CommonResponse<GestureResponse>> getGestureInfo(
            @PathVariable("memberGestureId") Long memberGestureId,
            @RequestParam Long memberId) {
        GestureResponse response = gestureService.getGestureInfo(memberId, memberGestureId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

}

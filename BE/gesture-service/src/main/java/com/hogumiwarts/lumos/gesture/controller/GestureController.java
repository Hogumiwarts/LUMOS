package com.hogumiwarts.lumos.gesture.controller;

import com.hogumiwarts.lumos.gesture.docs.GestureApiSpec;
import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import com.hogumiwarts.lumos.gesture.service.GestureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gesture")
@RequiredArgsConstructor
public class GestureController implements GestureApiSpec {

    private final GestureService gestureService;

    @GetMapping
    public ResponseEntity<List<GestureResponse>> getGestures(@RequestParam Long memberId) {
        List<GestureResponse> gestures = gestureService.getGestures(memberId);
        return ResponseEntity.ok(gestures);
    }

    @GetMapping("/{gestureId}")
    public ResponseEntity<GestureResponse> getGestureInfo(@RequestParam Long memberId,
                                                          @PathVariable Long gestureId) {
        GestureResponse response = gestureService.getGestureInfo(memberId, gestureId);
        return ResponseEntity.ok(response);
    }

}

package com.hogumiwarts.lumos.routine.controller;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.routine.docs.RoutineApiSpec;
import com.hogumiwarts.lumos.routine.dto.*;
import com.hogumiwarts.lumos.routine.service.RoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/routine")
@RequiredArgsConstructor
public class RoutineController implements RoutineApiSpec {

    private final RoutineService routineService;

    // 루틴 생성
    @PostMapping
    public ResponseEntity<CommonResponse<RoutineCreateResponse>> createRoutine(
        @RequestBody RoutineCreateRequest request) {
        RoutineCreateResponse routine  = routineService.createRoutine(request);
        return ResponseEntity.ok(CommonResponse.ok("루틴이 성공적으로 생성되었습니다.", routine));
    }

    // 루틴 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<List<RoutineListResponse>>> getRoutines() {
        List<RoutineListResponse> routines = routineService.getRoutineList();
        return ResponseEntity.ok(CommonResponse.ok(routines));
    }

    // 루틴 상세 조회
    @GetMapping("/{routineId}")
    public ResponseEntity<CommonResponse<RoutineDetailResponse>> getRoutine(@PathVariable Long routineId) {
        RoutineDetailResponse routines = routineService.getRoutine(routineId);
        return ResponseEntity.ok(CommonResponse.ok(routines));
    }

    // 루틴 생성
    @PutMapping("/{routineId}")
    public ResponseEntity<CommonResponse<RoutineCreateResponse>> updateRoutine(
        @PathVariable Long routineId,
        @RequestBody RoutineUpdateRequest request
    ) {
        RoutineCreateResponse routine  = routineService.updateRoutine(routineId, request);
        return ResponseEntity.ok(CommonResponse.ok("루틴이 성공적으로 수정되었습니다.", routine));
    }

    // 루틴 삭제
    @DeleteMapping("/{routineId}")
    public ResponseEntity<CommonResponse<SuccessResponse>> deleteRoutine(@PathVariable Long routineId) {
        routineService.deleteRoutine(routineId);
        return ResponseEntity.ok(CommonResponse.ok("루틴이 성공적으로 삭제되었습니다.", SuccessResponse.of(true)));
    }

    // 루틴 실행
    @PostMapping("/execute")
    public ResponseEntity<CommonResponse<SuccessResponse>> executeRoutine(@RequestParam Long gestureId) {
        routineService.executeRoutineByGestureId(gestureId);
        return ResponseEntity.ok(CommonResponse.ok("루틴이 성공적으로 실행되었습니다.", SuccessResponse.of(true)));
    }

    @GetMapping("/by-gesture")
    public RoutineResponse getRoutineByMemberIdAndGestureId(
        @RequestParam("memberId") Long memberId,
        @RequestParam("gestureId") Long gestureId
    ) {
		return routineService.getRoutineByMemberIdAndGestureId(memberId, gestureId);
    }
}

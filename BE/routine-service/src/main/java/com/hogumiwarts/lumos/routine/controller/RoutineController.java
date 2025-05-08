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
    public ResponseEntity<CommonResponse<SuccessResponse>> createRoutine(
            @RequestParam Long memberId,
            @RequestBody RoutineCreateRequest routineCreateRequest) {

        SuccessResponse response = routineService.createRoutine(memberId, routineCreateRequest);
        return ResponseEntity.ok(CommonResponse.ok(response));

    }

    // 루틴 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<List<RoutineResponse>>> getRoutines(
            @RequestParam Long memberId
    ) {
        List<RoutineResponse> routines = routineService.getRoutineList(memberId);
        return ResponseEntity.ok(CommonResponse.ok(routines));
    }

    // 루틴 삭제
    @DeleteMapping("/{routineId}")
    public ResponseEntity<CommonResponse<SuccessResponse>> deleteRoutine(
            @PathVariable Long routineId,
            @RequestParam Long memberId
    ) {
        SuccessResponse response = routineService.deleteRoutine(routineId, memberId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }


    // 루틴 수정
    @PatchMapping("/{routineId}")
    public ResponseEntity<CommonResponse<SuccessResponse>> patchRoutine(
            @PathVariable Long routineId,
            @RequestParam Long memberId,
            @RequestBody RoutineUpdateRequest request
    ) {
        SuccessResponse response = routineService.updateRoutine(routineId, memberId, request);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }


    // 루틴별 기기 정보 조회
    @GetMapping("/{routineId}/devices")
    public ResponseEntity<CommonResponse<RoutineDevicesResponse>> getRoutineDevices(
            @RequestParam Long memberId,
            @PathVariable Long routineId
    ) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long memberId = Long.valueOf(authentication.getName());

        RoutineDevicesResponse response = routineService.getRoutineDevices(memberId, routineId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

}

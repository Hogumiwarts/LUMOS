package com.hogumiwarts.lumos.routine.controller;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.routine.docs.RoutineApiSpec;
import com.hogumiwarts.lumos.routine.dto.SuccessResponse;
import com.hogumiwarts.lumos.routine.dto.RoutineRequest;
import com.hogumiwarts.lumos.routine.dto.RoutineResponse;
import com.hogumiwarts.lumos.routine.service.RoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routine")
@RequiredArgsConstructor
public class RoutineController implements RoutineApiSpec {

    private final RoutineService routineService;

    // 루틴 생성
    @PostMapping
    public ResponseEntity<CommonResponse<SuccessResponse>> createRoutine(
            @RequestParam Long memberId,
            @RequestBody RoutineRequest routineRequest) {

        SuccessResponse response = routineService.createRoutine(memberId, routineRequest);
        return ResponseEntity.ok(CommonResponse.ok(response));

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

    // 루틴별 기기 정보 조회
    @GetMapping("/{routineId}/devices")
    public ResponseEntity<CommonResponse<RoutineResponse>> getRoutineDevices(
            @RequestParam Long memberId,
            @PathVariable Long routineId
    ) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long memberId = Long.valueOf(authentication.getName());

        RoutineResponse response = routineService.getRoutines(memberId, routineId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

}

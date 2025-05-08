package com.hogumiwarts.lumos.routine.docs;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.routine.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "루틴 정보 조회", description = "루틴 정보 조회 API입니다.")
public interface RoutineApiSpec {

    @Operation(summary = "루틴 생성", description = """
            💡 루틴 만들기
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    ResponseEntity<CommonResponse<SuccessResponse>> createRoutine(
            @RequestParam Long memberId,
            @RequestBody RoutineCreateRequest routineCreateRequest
    );

    @Operation(summary = "루틴 목록 조회", description = """
            💡 루틴 리스트 조회
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    ResponseEntity<CommonResponse<List<RoutineResponse>>> getRoutines(
            @RequestParam Long memberId
    );

    // RoutineApiSpec.java (interface)
    @Operation(summary = "루틴 일부 수정", description = """
        ✏️ 루틴 일부 필드만 수정합니다. (PATCH)
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PatchMapping("/{routineId}")
    ResponseEntity<CommonResponse<SuccessResponse>> patchRoutine(
            @PathVariable Long routineId,
            @RequestParam Long memberId,
            @RequestBody RoutineUpdateRequest routineUpdateRequest
    );



    @Operation(summary = "루틴 삭제", description = """
            💡 루틴 삭제하기
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    ResponseEntity<CommonResponse<SuccessResponse>> deleteRoutine(
            @PathVariable Long routineId,
            @RequestParam Long memberId
    );

    @Operation(summary = "루틴 정보 조회", description = """
            💡 routineId로 루틴 정보를 조회합니다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    ResponseEntity<CommonResponse<RoutineDevicesResponse>> getRoutineDevices(
            @RequestParam Long memberId,
            @PathVariable Long routineId
    );

}

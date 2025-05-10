package com.hogumiwarts.lumos.routine.docs;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.routine.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "루틴", description = "루틴 관리 API")
public interface RoutineApiSpec {

    @Operation(summary = "루틴 생성", description = """
            💡 루틴을 생성합니다.
            - 기존의 루틴과 연결된 제스처를 연결할 경우 기존 루틴의 제스처 연결을 해제하고 새로운 루틴에 연결합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = RoutineCreateResponse.class))),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다. (예: 존재하지 않는 제스처)", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content)
    })
    ResponseEntity<?> createRoutine(@RequestBody RoutineCreateRequest routineCreateRequest);

    @Operation(summary = "루틴 목록 조회", description = """
            💡 루틴 전체 목록을 조회합니다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content)
    })
    ResponseEntity<?> getRoutines();

    @Operation(summary = "루틴 상세 조회", description = """
            💡 `routineId`를 통해 루틴의 상세 정보를 조회합니다.
            """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다.", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content)
    })
    ResponseEntity<?> getRoutine(@PathVariable Long routineId);

    @Operation(
        summary = "루틴 수정", description = """
            💡 루틴을 수정합니다.
            - 기존의 루틴과 연결된 제스처를 연결할 경우 기존 루틴의 제스처 연결을 해제하고 새로운 루틴에 연결합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = RoutineCreateResponse.class))),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다. (예: 존재하지 않는 제스처)", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content)
    })
    ResponseEntity<?> updateRoutine(
        @PathVariable Long routineId,
        @RequestBody RoutineUpdateRequest request
    );

    @Operation(summary = "루틴 삭제", description = """
            💡 루틴을 삭제합니다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content)
    })
    ResponseEntity<?> deleteRoutine(@PathVariable Long routineId);

    @Operation(summary = "X", description = """
            💡 `memberId`와 `gestureId`에 해당하는 루틴의 id와 이름을 반환합니다.
            """)
    RoutineResponse getRoutineByMemberIdAndGestureId(
        @RequestParam("memberId") Long memberId,
        @RequestParam("gestureId") Long gestureId
    );
}

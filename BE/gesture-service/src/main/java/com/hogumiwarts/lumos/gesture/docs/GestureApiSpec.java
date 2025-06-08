package com.hogumiwarts.lumos.gesture.docs;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "제스처", description = "제스처 관리 API")
public interface GestureApiSpec {

    @Operation(summary = "제스처 목록 조회", description = """
            💡 사용자의 제스처 + 루틴 정보를 반환합니다.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 형식입니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    ResponseEntity<?> getGestures();

    @Operation(summary = "X", description = """
            💡 `gestureId`로 제스처 정보를 조회합니다.
            """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "제스처 정보 반환 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<?> getGesture(@Parameter(description = "제스처 ID", example = "1") @PathVariable("gestureId") Long gestureId);
}

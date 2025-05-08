package com.hogumiwarts.lumos.gesture.docs;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "제스처 조회", description = "회원 ID로 제스처 목록을 조회합니다.")
public interface GestureApiSpec {

    @Operation(summary = "제스처 목록 조회", description = "회원 ID를 기반으로 제스처 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "센서 데이터 저장 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 형식입니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    ResponseEntity<CommonResponse<List<GestureResponse>>> getGestures(
            @Parameter(description = "회원 ID", example = "1")
            @RequestParam Long memberId
    );

    @Operation(summary = "단일 제스처 조회", description = "제스처 ID로 제스처 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제스처 정보 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<GestureResponse>> getGestureInfo(
            @Parameter(description = "회원 ID", example = "1") @RequestParam Long memberId,
            @Parameter(description = "제스처 ID", example = "1") @PathVariable Long memberGestureId
    );


}

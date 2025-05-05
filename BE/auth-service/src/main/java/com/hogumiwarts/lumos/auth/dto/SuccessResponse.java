package com.hogumiwarts.lumos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessResponse {

	@Schema(description = "요청 처리 성공 여부", example = "true")
	private final Boolean success;

	/**
	 * 성공 응답을 생성하는 정적 메서드
	 */
	public static SuccessResponse success() {
		return SuccessResponse.builder()
			.success(true)
			.build();
	}

	/**
	 * 실패 응답을 생성하는 정적 메서드
	 */
	public static SuccessResponse failure() {
		return SuccessResponse.builder()
			.success(false)
			.build();
	}
}
package com.hogumiwarts.lumos.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class LoginResponse {
	@Schema(description = "사용자 ID")
	private Long memberId;

	@Schema(description = "사용자 이메일")
	private String email;

	@Schema(description = "사용자 이름")
	private String name;

	@Schema(description = "액세스 토큰")
	private String accessToken;

	@Schema(description = "리프레시 토큰")
	private String refreshToken;
}
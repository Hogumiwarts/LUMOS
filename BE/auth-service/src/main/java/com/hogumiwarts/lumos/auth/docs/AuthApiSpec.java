package com.hogumiwarts.lumos.auth.docs;

import com.hogumiwarts.lumos.auth.dto.LoginRequest;
import com.hogumiwarts.lumos.auth.dto.LoginResponse;
import com.hogumiwarts.lumos.auth.dto.SignupRequest;
import com.hogumiwarts.lumos.auth.dto.SignupResponse;
import com.hogumiwarts.lumos.auth.dto.SuccessResponse;
import com.hogumiwarts.lumos.auth.dto.TokenRefreshRequest;
import com.hogumiwarts.lumos.auth.dto.TokenRefreshResponse;
import com.hogumiwarts.lumos.dto.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증", description = "인증 및 회원관리 API")
public interface AuthApiSpec {

	@Operation(
		summary = "회원 가입",
		description = """
        💡 사용자가 회원 가입을 요청합니다.

        - 이메일, 비밀번호, 이름을 입력받아 회원을 생성합니다.
        - 내부적으로 이메일 중복 여부를 확인하고 비밀번호 일치 여부를 검증합니다.
        """
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원 가입이 완료되었습니다."),
		@ApiResponse(responseCode = "400",
			description = """
				`[SIGNUP-001]` 이미 존재하는 이메일입니다.
				
				`[SIGNUP-002]` 비밀번호가 일치하지 않습니다.
				""",
			content = @Content()),
		@ApiResponse(responseCode = "500",
			description = "`[SIGNUP-003]` 회원 가입 처리 중 서버 내부 오류가 발생했습니다.",
			content = @Content())
	})
	ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request);

	@Operation(summary = "로그인", description = """
		💡 사용자가 로그인을 진행합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인에 성공했습니다."),
		@ApiResponse(responseCode = "400", description = "`[LOGIN-001]` 잘못된 비밀번호입니다.", content = @Content()),
		@ApiResponse(responseCode = "404", description = "`[LOGIN-002]` 해당 이메일을 가진 사용자가 없습니다.", content = @Content()),
		@ApiResponse(responseCode = "500",
			description = "`[LOGIN-003]` 로그인 처리 중 서버 오류가 발생했습니다.",
			content = @Content())
	})
	ResponseEntity<?> login(@Valid @RequestBody LoginRequest request);

	@Operation(summary = "로그아웃", description = """
		💡 사용자가 로그아웃을 진행합니다.
		
		---
		
		[ 참고 ]
		- 로그아웃 시 Authorization 헤더에 Access Token을 포함해야 합니다.
			- `Authorization: Bearer {accessToken}` 형식
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다."),
		@ApiResponse(responseCode = "401",
			description = """
				`[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.
				
				`[AUTH-002]` 유효하지 않은 Access Token입니다.
				
				`[AUTH-003]` 만료된 Access Token입니다.
				
				`[AUTH-004]` Access Token이 누락되었습니다.
				""",
			content = @Content()),
		@ApiResponse(responseCode = "500", description = "`[LOGOUT-001]` 로그아웃 처리 중 서버 오류가 발생했습니다.", content = @Content())
	})
	ResponseEntity<?> logout(HttpServletRequest request);

	@Operation(
		summary = "Access Token 재발급",
		description = """
        💡 사용자가 리프레시 토큰을 기반으로 새 Access Token을 발급합니다.
        - 기존 Refresh Token이 유효해야 하며 Redis에 저장된 값과 일치해야 합니다.
        """
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰이 성공적으로 재발급되었습니다."),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
		@ApiResponse(responseCode = "401", description = "만료된 Refresh Token입니다.", content = @Content),
		@ApiResponse(responseCode = "401", description = "잘못된 JWT 서명입니다.", content = @Content)
	})
	ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request);
}

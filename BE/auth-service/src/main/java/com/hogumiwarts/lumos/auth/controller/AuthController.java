package com.hogumiwarts.lumos.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hogumiwarts.lumos.auth.docs.AuthApiSpec;
import com.hogumiwarts.lumos.auth.dto.LoginRequest;
import com.hogumiwarts.lumos.auth.dto.LoginResponse;
import com.hogumiwarts.lumos.auth.dto.SignupRequest;
import com.hogumiwarts.lumos.auth.dto.SignupResponse;
import com.hogumiwarts.lumos.auth.dto.SuccessResponse;
import com.hogumiwarts.lumos.auth.dto.TokenRefreshRequest;
import com.hogumiwarts.lumos.auth.dto.TokenRefreshResponse;
import com.hogumiwarts.lumos.auth.service.AuthService;
import com.hogumiwarts.lumos.dto.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController implements AuthApiSpec {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
		SignupResponse response = authService.signup(request);
		return ResponseEntity.ok(CommonResponse.ok("회원 가입이 완료되었습니다.", response));
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(CommonResponse.ok("로그인에 성공했습니다.", response));
	}

	@PostMapping("/logout")
	public ResponseEntity<CommonResponse<SuccessResponse>> logout(HttpServletRequest request) {
		authService.logout(request);
		return ResponseEntity.ok(CommonResponse.ok("로그아웃에 성공했습니다.", SuccessResponse.success()));
	}

	@PostMapping("/refresh")
	public ResponseEntity<CommonResponse<TokenRefreshResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
		TokenRefreshResponse response = authService.reissueAccessToken(request);
		return ResponseEntity.ok(CommonResponse.ok("토큰이 성공적으로 재발급되었습니다.", response));
	}
}

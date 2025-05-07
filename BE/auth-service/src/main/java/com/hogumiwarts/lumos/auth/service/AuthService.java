package com.hogumiwarts.lumos.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hogumiwarts.lumos.auth.client.MemberClient;
import com.hogumiwarts.lumos.auth.dto.CreateMemberRequest;
import com.hogumiwarts.lumos.auth.dto.LoginRequest;
import com.hogumiwarts.lumos.auth.dto.LoginResponse;
import com.hogumiwarts.lumos.auth.dto.MemberResponse;
import com.hogumiwarts.lumos.auth.dto.SignupRequest;
import com.hogumiwarts.lumos.auth.dto.SignupResponse;
import com.hogumiwarts.lumos.auth.dto.TokenRefreshRequest;
import com.hogumiwarts.lumos.auth.dto.TokenRefreshResponse;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import com.hogumiwarts.lumos.jwt.JwtTokenProvider;
import com.hogumiwarts.lumos.redis.RedisTokenService;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberClient memberClient;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTokenService redisTokenService;

	public SignupResponse signup(SignupRequest request) {
		if (memberClient.checkEmailExists(request.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		if (!request.getPassword1().equals(request.getPassword2())) {
			throw new CustomException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
		}

		String encodedPw = passwordEncoder.encode(request.getPassword1());

		CreateMemberRequest createRequest = new CreateMemberRequest();
		createRequest.setEmail(request.getEmail());
		createRequest.setPassword(encodedPw);
		createRequest.setName(request.getName());

		MemberResponse created = memberClient.createMember(createRequest);

		return new SignupResponse(
			created.getMemberId(),
			created.getEmail(),
			created.getName(),
			created.getCreatedAt()
		);
	}

	public LoginResponse login(LoginRequest request) {
		// 1. 사용자 조회 (Feign으로 회원 정보 가져오기)
		MemberResponse member = memberClient.findByEmail(request.getEmail());

		if (member == null) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new CustomException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
		}

		// 3. JWT 생성
		String accessToken = jwtTokenProvider.generateAccessToken(member.getMemberId());
		String refreshToken = jwtTokenProvider.generateRefreshToken(member.getMemberId());

		// Redis에 리프레시 토큰 저장 (만료 시간 설정)
		redisTokenService.saveRefreshToken(member.getMemberId(), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

		return LoginResponse.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.name(member.getName())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * [로그아웃]
	 * → access token 추출
	 * → 남은 시간만큼 redis에 블랙리스트 등록
	 * → refresh token 삭제
	 */
	public void logout(HttpServletRequest request) {
		String token = jwtTokenProvider.resolveToken(request);
		if (token == null || token.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
		}

		// 1. AccessToken 만료 시간 계산
		long expiration = jwtTokenProvider.getTokenRemainingTime(token);

		// 2. 블랙리스트 등록
		redisTokenService.blacklistAccessToken(token, expiration);

		// 3. RefreshToken 삭제 (subject = email)
		Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
		redisTokenService.deleteRefreshToken(memberId);
	}

	public TokenRefreshResponse reissueAccessToken(TokenRefreshRequest request) {
		String refreshToken = request.refreshToken();

		// 1. Refresh Token 유효성 검증
		jwtTokenProvider.validateRefreshToken(refreshToken);

		// 2. 토큰에서 memberId 추출
		Long memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);

		// 3. FeignClient 통해 member 존재 여부 확인
		MemberResponse member = memberClient.getMember(memberId);
		if (member == null) {
			throw new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND);
		}

		// 4. Redis에서 저장된 refreshToken과 비교
		String storedToken = redisTokenService.getRefreshToken(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));

		if (!storedToken.equals(refreshToken)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}

		// 5. 새로운 Access Token 발급
		String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);

		return new TokenRefreshResponse(newAccessToken);
	}
}

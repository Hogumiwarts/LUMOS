package com.hogumiwarts.lumos.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hogumiwarts.lumos.redis.RedisTokenService;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 이 필터는 Spring Security에서 HTTP 요청이 들어올 때마다 실행됨
 * OncePerRequestFilter를 상속받았기 때문에 모든 요청마다 한 번씩 실행됨
 * 클라이언트가 API 요청을 보낼 때, JWT 인증을 검사하고 인증 객체를 설정하는 역할을 수행
 * [클라이언트 요청] → [JwtAuthenticationFilter] → [Spring Security 인증 검사] → [Controller 실행]
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTokenService redisTokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String path = request.getRequestURI();

		// Swagger, API Docs, 정적 자원, 인증 관련 경로는 JWT 검사에서 제외
		if (isExcludePath(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = resolveToken(request); // 헤더에서 토큰 추출

		if (token != null && !token.trim().isEmpty()) {
			try {
				// AccessToken 유효성 검사
				if (jwtTokenProvider.validateAccessToken(token)) {

					// 블랙리스트에 있는 토큰인지 확인
					if (redisTokenService.isAccessTokenBlacklisted(token)) {
						request.setAttribute("exception", new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED));
						filterChain.doFilter(request, response);
						return;
					}

					// 정상적인 토큰일 경우 인증 객체 설정
					Authentication authentication = jwtTokenProvider.getAuthentication(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (CustomException e) {
				request.setAttribute("exception", e);
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);  // "Bearer " 제거 후 반환
		}
		return null;
	}

	private boolean isExcludePath(String path) {
		return path.contains("/swagger-ui")
			|| path.contains("/v3/api-docs")
			|| path.contains("/swagger-resources")
			|| path.contains("/webjars");
	}
}

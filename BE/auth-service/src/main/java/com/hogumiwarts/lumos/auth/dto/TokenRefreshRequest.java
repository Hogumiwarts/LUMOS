package com.hogumiwarts.lumos.auth.dto;

public record TokenRefreshRequest(
	Long memberId,
	String refreshToken
) {}
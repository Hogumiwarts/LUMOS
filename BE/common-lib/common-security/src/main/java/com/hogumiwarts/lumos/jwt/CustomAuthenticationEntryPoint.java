package com.hogumiwarts.lumos.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogumiwarts.lumos.dto.ErrorResponse;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException)
		throws IOException {

		Throwable exception = (Throwable)request.getAttribute("exception");

		ErrorCode errorCode = ErrorCode.UNAUTHORIZED_USER;

		if (exception instanceof CustomException customEx) {
			errorCode = customEx.getErrorCode();
		}

		ErrorResponse errorResponse = ErrorResponse.of(errorCode);

		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json;charset=UTF-8");

		ObjectMapper objectMapper = new ObjectMapper();
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}

package com.hogumiwarts.lumos.routine.exception;

import com.hogumiwarts.lumos.dto.ErrorResponse;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// CustomException 처리 (ErrorCode 활용)
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus().value())
			.body(ErrorResponse.of(errorCode));
	}

	// 400 - 잘못된 요청 (입력 값 오류)
	// 유효성 검사 실패 예외 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();

		// 필드별 오류 메시지 리스트 생성
		List<ErrorResponse.FieldErrorDetail> fieldErrors = bindingResult.getFieldErrors().stream()
			.map(error -> ErrorResponse.FieldErrorDetail.builder()
				.field(error.getField())
				.message(error.getDefaultMessage())
				.build())
			.collect(Collectors.toList());

		// 유효성 검사 실패 ErrorResponse 생성
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, fieldErrors));
	}

	// 500 - 서버 내부 오류
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		log.error("Unhandled Exception", ex);
		return ResponseEntity
			.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
			.body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
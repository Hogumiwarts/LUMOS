package com.hogumiwarts.lumos.device.exception;

import java.util.List;
import java.util.stream.Collectors;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hogumiwarts.lumos.dto.ErrorResponse;
import com.hogumiwarts.lumos.exception.CustomException;
import com.hogumiwarts.lumos.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// SmartThings API : 디바이스 제어 불가능 상태일 때 에러처리
//	@ExceptionHandler(FeignException.class)
//	public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
//		log.warn("FeignException 발생: status={}, message={}", ex.status(), ex.contentUTF8());
//
//		// 409 Conflict - invalid device state
//		if (ex.status() == 409 && ex.contentUTF8().contains("invalid device state")) {
//			return ResponseEntity
//					.status(ErrorCode.DEVICE_INVALID_STATE.getStatus().value())
//					.body(ErrorResponse.of(ErrorCode.DEVICE_INVALID_STATE));
//		}
//
//		// 403 Forbidden - 권한 없음 또는 연결 해제 등
//		if (ex.status() == 403) {
//			return ResponseEntity
//					.status(ErrorCode.DEVICE_FORBIDDEN.getStatus().value())
//					.body(ErrorResponse.of(ErrorCode.DEVICE_FORBIDDEN));
//		}
//
//		// 그 외 Feign 에러는 일반 서버 에러로 처리
//		log.error("Unhandled FeignException", ex);
//		return ResponseEntity
//				.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
//				.body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
//	}

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
package com.hogumiwarts.lumos.gesturesensor.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "제스처 센서 데이터", description = "워치에서 수집한 센서 데이터를 저장하는 API입니다.")
public interface SensorDataApiSpec {

	@Operation(summary = "센서 데이터 저장", description = """
		💡 워치에서 수집된 가속도/자이로 데이터를 저장합니다.
		- 센서 데이터는 시계열로 구성된 JSON 배열이며, 각 항목은 타임스탬프와 x/y/z축 센서값을 포함합니다.
		- gestureId로 제스처 유형을 식별합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "센서 데이터 저장 성공"),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 입력 형식입니다."),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
	})
	ResponseEntity<Void> saveSensorData(@Valid @RequestBody SensorDataRequest request) throws JsonProcessingException;
}

package com.hogumiwarts.lumos.gesturesensor.docs;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.dto.ErrorResponse;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@Tag(name = "제스처 센서 데이터", description = "워치 센서 데이터 수집 API")
public interface SensorDataApiSpec {

	@Operation(summary = "센서 데이터 저장", description = """
		💡 워치에서 수집된 가속도/자이로 데이터를 저장 합니다.
		- 센서 데이터는 시계열로 구성된 JSON 배열이며 각 항목은 타임스탬프와 x/y/z축 센서 값을 포함합니다.
		- gestureId로 제스처 유형을 식별합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "센서 데이터 저장 성공", content = @Content()),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 입력 형식", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류 발생", content = @Content())
	})
	ResponseEntity<?> saveSensorData(
		@Valid @RequestBody SensorDataRequest request) throws
		JsonProcessingException;

	@Operation(
		summary = "제스처 데이터셋 폴더 압축 다운로드",
		description = """
        💡 S3의 gesture_dataset/{folder}/ 하위의 CSV 파일들을 ZIP으로 묶어 다운로드 합니다.

        - 예: folder=1 → gesture_dataset/1/ 경로에 있는 파일들을 압축
        """
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "압축 성공 및 다운로드",
			content = @Content()
		)
	})
	public ResponseEntity<?> downloadZip() throws IOException;
}

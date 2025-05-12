package com.hogumiwarts.lumos.gesturesensor.docs;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.dto.ErrorResponse;
import com.hogumiwarts.lumos.gesturesensor.dto.PredictionResult;
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

	@Operation(
		summary = "센서 데이터 저장 및 제스처 예측",
		description = """
		💡 워치에서 수집된 가속도/자이로 데이터를 저장하고 해당 데이터를 기반으로 제스처 분류 모델을 통해 예측을 수행합니다.

		- 센서 데이터는 시계열로 구성된 JSON 배열이며 각 항목은 타임스탬프와 x/y/z축 센서 값을 포함합니다.
		
		- `gestureId`는 해당 데이터가 어떤 제스처인지 나타내며 이를 기준으로 예측 결과와 비교해 정확도를 판단합니다.

		- 저장된 데이터는 추후 학습 및 분석을 위한 용도로 사용될 수 있습니다.
		
		---
		
		[응답 데이터]
		
		- `ground_truth`: 요청에서 전달된 실제 정답 제스처 ID
		- `predicted`: 모델이 예측한 제스처 ID
		- `match`: 예측이 정답과 일치하는지 여부 (true/false)
		"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "센서 데이터 저장 및 예측 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = PredictionResult.class),
				examples = @ExampleObject(value = """
				{
				  "ground_truth": 1,
				  "predicted": 1,
				  "match": true
				}
			""")
			)
		),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 입력 형식", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류 발생", content = @Content())
	})
	ResponseEntity<?> saveSensorData(
		@Valid @RequestBody SensorDataRequest request
	) throws JsonProcessingException;

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

	@Operation(
		summary = "제스처 분류 모델 추론",
		description = """
		💡 워치에서 전송된 센서 데이터를 받아서 모델 추론을 진행합니다.

		---
		
		[응답 데이터]
		
		- `ground_truth`: 요청에서 전달된 실제 정답 제스처 ID
		- `predicted`: 모델이 예측한 제스처 ID
		- `match`: 예측이 정답과 일치하는지 여부 (true/false)
		"""
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "제스처 추론 성공",
			content = @Content(schema = @Schema(implementation = PredictionResult.class))),
		@ApiResponse(responseCode = "422", description = "입력 JSON 형식 오류", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content())
	})
	ResponseEntity<?> predictGesture(@RequestBody SensorDataRequest request) throws JsonProcessingException;
}

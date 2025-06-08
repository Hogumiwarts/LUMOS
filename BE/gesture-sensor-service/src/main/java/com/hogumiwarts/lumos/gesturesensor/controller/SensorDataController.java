package com.hogumiwarts.lumos.gesturesensor.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.gesturesensor.docs.SensorDataApiSpec;
import com.hogumiwarts.lumos.gesturesensor.dto.PredictionResult;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.hogumiwarts.lumos.gesturesensor.service.GestureSensorDataService;
import com.hogumiwarts.lumos.gesturesensor.service.S3DownloadService;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorDataController implements SensorDataApiSpec {

	private final GestureSensorDataService sensorDataService;
	private final S3DownloadService s3DownloadService;

	@PostMapping
	public ResponseEntity<CommonResponse<PredictionResult>> saveSensorData(@RequestBody SensorDataRequest request) throws JsonProcessingException {
		PredictionResult predictionResult = sensorDataService.saveSensorData(request);
		return ResponseEntity.ok(CommonResponse.ok("데이터가 성공적으로 저장되었습니다.", predictionResult));
	}

	@GetMapping("/download")
	public ResponseEntity<StreamingResponseBody> downloadZip() throws IOException {
		Path zipFile = s3DownloadService.zipGestureDataset(); // 압축만 수행, 업로드 안 함

		StreamingResponseBody stream = outputStream -> {
			try (InputStream inputStream = new FileInputStream(zipFile.toFile())) {
				inputStream.transferTo(outputStream); // 파일을 클라이언트로 스트리밍
			} finally {
				Files.deleteIfExists(zipFile); // 전송 끝난 뒤 삭제
			}
		};

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getFileName())
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(stream);
	}

	@PostMapping("/predict")
	public ResponseEntity<CommonResponse<PredictionResult>> predictGesture(@RequestBody SensorDataRequest request) throws JsonProcessingException {
		PredictionResult predictionResult = sensorDataService.predictGesture(request);
		return ResponseEntity.ok(CommonResponse.ok("성공적으로 추론되었습니다.", predictionResult));
	}
}

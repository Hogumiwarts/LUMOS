package com.hogumiwarts.lumos.gesturesensor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hogumiwarts.lumos.gesturesensor.client.GestureClassificationClient;
import com.hogumiwarts.lumos.gesturesensor.dto.PredictionResult;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.hogumiwarts.lumos.gesturesensor.entity.GestureSensorData;
import com.hogumiwarts.lumos.gesturesensor.repository.GestureSensorDataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class GestureSensorDataService {

	@Value("${sensor.csv-upload-enabled:false}")
	private boolean uploadEnabled;

	private final GestureSensorDataRepository repository;
	private final S3UploadService s3UploadService;
	private final GestureClassificationClient gestureClassificationClient;

	private final ObjectMapper snakeCaseMapper = new ObjectMapper()
		.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
		.registerModule(new JavaTimeModule())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public PredictionResult saveSensorData(SensorDataRequest request) throws JsonProcessingException {
		GestureSensorData entity = GestureSensorData.builder()
			.gestureId(request.getGestureId())
			.watchDeviceId(request.getWatchDeviceId())
			.data(snakeCaseMapper.writeValueAsString(request.getData()))
			.build();

		repository.save(entity);

		if (uploadEnabled) {
			String url = s3UploadService.saveCsvAndUpload(request);
		}

		// snake_case로 JSON 직렬화 후 전달
		String snakeCaseJson = snakeCaseMapper.writeValueAsString(request);
		return gestureClassificationClient.predict(snakeCaseJson);
	}

	public PredictionResult predictGesture(SensorDataRequest request) throws JsonProcessingException {
		String snakeCaseJson = snakeCaseMapper.writeValueAsString(request);
		return gestureClassificationClient.predict(snakeCaseJson);
	}
}

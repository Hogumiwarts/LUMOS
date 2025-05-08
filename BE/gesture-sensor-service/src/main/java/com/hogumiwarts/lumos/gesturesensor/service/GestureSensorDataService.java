package com.hogumiwarts.lumos.gesturesensor.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.hogumiwarts.lumos.gesturesensor.entity.GestureSensorData;
import com.hogumiwarts.lumos.gesturesensor.repository.GestureSensorDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GestureSensorDataService {

	private final GestureSensorDataRepository repository;
	private final S3UploadService s3UploadService;

	private final ObjectMapper snakeCaseMapper = new ObjectMapper()
		.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
		.registerModule(new JavaTimeModule())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public void saveSensorData(SensorDataRequest request) throws JsonProcessingException {
		GestureSensorData entity = GestureSensorData.builder()
			.gestureId(request.getGestureId())
			.watchDeviceId(request.getWatchDeviceId())
			.data(snakeCaseMapper.writeValueAsString(request.getData()))
			.build();

		repository.save(entity);

		String url = s3UploadService.saveCsvAndUpload(request);
	}
}

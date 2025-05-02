package com.hogumiwarts.lumos.gesturesensor.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.hogumiwarts.lumos.gesturesensor.entity.GestureSensorData;
import com.hogumiwarts.lumos.gesturesensor.repository.GestureSensorDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GestureSensorDataService {

	private final GestureSensorDataRepository repository;

	public void saveSensorData(SensorDataRequest request) throws JsonProcessingException {
		GestureSensorData entity = GestureSensorData.builder()
			.gestureId(request.getGestureId())
			.watchDeviceId(request.getWatchDeviceId())
			.data(new ObjectMapper().writeValueAsString(request.getData()))
			.build();

		repository.save(entity);
	}
}

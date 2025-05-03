package com.hogumiwarts.lumos.gesturesensor.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hogumiwarts.lumos.gesturesensor.docs.SensorDataApiSpec;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;
import com.hogumiwarts.lumos.gesturesensor.service.GestureSensorDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorDataController implements SensorDataApiSpec {

	private final GestureSensorDataService sensorDataService;

	@PostMapping
	public ResponseEntity<Map<String, String>> saveSensorData(@RequestBody SensorDataRequest request) throws
		JsonProcessingException {
		sensorDataService.saveSensorData(request);
		return ResponseEntity.ok(Map.of("status", "success"));
	}
}

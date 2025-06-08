package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.AirPurifierApiSpec;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierDetailResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierFanModeResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.AirPurifierStatusResponse;
import com.hogumiwarts.lumos.device.dto.airpurifier.FanModeControlRequest;
import com.hogumiwarts.lumos.device.dto.PowerControlRequest;
import com.hogumiwarts.lumos.device.dto.SuccessResponse;
import com.hogumiwarts.lumos.device.service.AirPurifierService;
import com.hogumiwarts.lumos.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class AirPurifierController implements AirPurifierApiSpec {

    private final AirPurifierService airPurifierService;

    @Override
    public ResponseEntity<CommonResponse<AirPurifierStatusResponse>> updateAirPurifierPower(Long deviceId, PowerControlRequest request) {
        AirPurifierStatusResponse response = airPurifierService.updateAirPurifierPower(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Override
    public ResponseEntity<CommonResponse<AirPurifierFanModeResponse>> updateAirPurifierFanMode(Long deviceId, FanModeControlRequest request) {
        AirPurifierFanModeResponse response = airPurifierService.updateAirPurifierFanMode(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Override
    public ResponseEntity<CommonResponse<AirPurifierDetailResponse>> getAirPurifierStatus(Long deviceId) {
        AirPurifierDetailResponse response = airPurifierService.getAirPurifierStatus(deviceId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}
package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.LightApiSpec;
import com.hogumiwarts.lumos.device.dto.*;
import com.hogumiwarts.lumos.device.service.LightService;
import com.hogumiwarts.lumos.device.util.ColorConverter;
import com.hogumiwarts.lumos.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class LightController implements LightApiSpec {

    private final LightService lightService;

    // onoff 변환
    @Override
    public ResponseEntity<CommonResponse<SuccessResponse>> updateLightStatus(Long deviceId, PowerControlRequest request) {
        lightService.updateLightStatus(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
    }

    // 색상 변환
    @Override
    public ResponseEntity<CommonResponse<SuccessResponse>> updateLightColor(Long deviceId, LightColorRequest request) {
        lightService.updateLightColor(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
    }

    // 색온도 변환
    @Override
    public ResponseEntity<CommonResponse<SuccessResponse>> updateLightTemperature(Long deviceId, LightTemperatureRequest request) {
        lightService.updateLightTemperature(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
    }

    // 밝기 변환
    @Override
    public ResponseEntity<CommonResponse<SuccessResponse>> updateLightBright(Long deviceId, LightBrightRequest request) {
        lightService.updateLightBrightness(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
    }

    // 조명 상태 조회
    @Override
    public ResponseEntity<CommonResponse<LightDetailResponse>> getLightStatus(Long deviceId) {
        LightDetailResponse response = lightService.getLightStatus(deviceId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}

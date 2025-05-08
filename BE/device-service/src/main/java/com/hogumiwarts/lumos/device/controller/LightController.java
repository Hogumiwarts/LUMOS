package com.hogumiwarts.lumos.device.controller;

import com.hogumiwarts.lumos.device.docs.LightApiSpec;
import com.hogumiwarts.lumos.device.dto.LightColorRequest;
import com.hogumiwarts.lumos.device.dto.LightDetailResponse;
import com.hogumiwarts.lumos.device.dto.PowerControlRequest;
import com.hogumiwarts.lumos.device.dto.SuccessResponse;
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

    // TODO: onoff 변환
    @Override
    public ResponseEntity<CommonResponse<SuccessResponse>> updateLightStatus(Long deviceId, PowerControlRequest request) {
        lightService.updateLightStatus(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
    }

    // TODO: 색상 변환
    @Override
    public ResponseEntity<CommonResponse<SuccessResponse>> updateLightColor(Long deviceId, LightColorRequest request) {
        lightService.updateLightColor(deviceId, request);
        return ResponseEntity.ok(CommonResponse.ok(SuccessResponse.success()));
    }


    // TODO: 조명 상태 조회
    @Override
    public ResponseEntity<?> getLightStatus(Long deviceId) {
        LightDetailResponse response = lightService.getLightStatus(deviceId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}

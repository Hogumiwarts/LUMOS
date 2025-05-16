package com.hogumiwarts.lumos.routine.client;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.routine.config.FeignAuthConfig;
import com.hogumiwarts.lumos.routine.dto.DevicesCreateResponse;
import com.hogumiwarts.lumos.routine.dto.DevicesResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "device-service", url = "${device.service.url}", configuration = FeignAuthConfig.class)
public interface DeviceServiceClient {

    @GetMapping("/api/devices/details")
    List<DevicesResponse> getDeviceDetailsByIds(@RequestParam("deviceIds") List<Long> deviceIds);
}
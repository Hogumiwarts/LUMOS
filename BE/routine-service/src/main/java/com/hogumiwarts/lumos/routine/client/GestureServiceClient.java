package com.hogumiwarts.lumos.routine.client;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.routine.config.FeignAuthConfig;
import com.hogumiwarts.lumos.routine.dto.GestureResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gesture-service", url = "${gesture.service.url}", configuration = FeignAuthConfig.class)
public interface GestureServiceClient {

    @GetMapping("/api/gesture/{gestureId}")
    CommonResponse<GestureResponse> getGesture(@PathVariable Long gestureId);
}
package com.hogumiwarts.lumos.gesture.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hogumiwarts.lumos.dto.CommonResponse;
import com.hogumiwarts.lumos.gesture.config.FeignAuthConfig;
import com.hogumiwarts.lumos.gesture.dto.RoutineResponse;

@FeignClient(name = "routine-service", url = "${routine.service.url}", configuration = FeignAuthConfig.class)
public interface RoutineServiceClient {

    @GetMapping("/api/routine/by-gesture")
   RoutineResponse getRoutineByGesture(
        @RequestParam("memberId") Long memberId,
        @RequestParam("gestureId") Long gestureId
    );
}
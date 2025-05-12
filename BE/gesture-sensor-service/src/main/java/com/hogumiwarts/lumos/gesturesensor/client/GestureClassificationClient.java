package com.hogumiwarts.lumos.gesturesensor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hogumiwarts.lumos.gesturesensor.dto.PredictionResult;
import com.hogumiwarts.lumos.gesturesensor.dto.SensorDataRequest;

@FeignClient(name = "gestureClient", url = "${gesture-classification.server.url}")
public interface GestureClassificationClient {
    @PostMapping(
        value = "/api/predict",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    PredictionResult predict(@RequestBody String  request);
}

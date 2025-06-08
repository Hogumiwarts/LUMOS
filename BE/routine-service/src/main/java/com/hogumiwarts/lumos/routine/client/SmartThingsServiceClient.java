package com.hogumiwarts.lumos.routine.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.routine.config.FeignAuthConfig;
import com.hogumiwarts.lumos.routine.dto.CommandExecuteRequest;

@FeignClient(name = "smartthings-service", url = "${smartthings.service.url}", configuration = FeignAuthConfig.class)
public interface SmartThingsServiceClient {

    @PostMapping("/devices/{controlDeviceId}/command")
    ResponseEntity<JsonNode> executeCommand(
            @PathVariable("controlDeviceId") String controlDeviceId,
            @RequestHeader("installedappid") String installedAppId,
            @RequestBody CommandExecuteRequest commandRequest
    );
}
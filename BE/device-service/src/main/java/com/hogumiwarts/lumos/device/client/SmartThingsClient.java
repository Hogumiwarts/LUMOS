package com.hogumiwarts.lumos.device.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.hogumiwarts.lumos.device.dto.CommandRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "smartthings-service", url = "${smartthings.service.url}")
public interface SmartThingsClient {

    @GetMapping("/devices")
    JsonNode getAllDevices(
//            @RequestParam(value = "controlDeviceId", required = false) String controlDeviceId,
            @RequestHeader("installedappid") String installedAppId
    );

    @GetMapping("/devices/{controlDeviceId}/status")
    JsonNode fetchDeviceStatus(
            @PathVariable("controlDeviceId") String controlDeviceId,
            @RequestHeader("installedappid") String installedAppId
    );

    @GetMapping("/devices/{controlDeviceId}/components/main/capabilities/{capabilityId}/commands")
    JsonNode fetchDeviceCommands(
            @PathVariable("controlDeviceId") String controlDeviceId,
            @PathVariable("capabilityId") String capabilityId,
            @RequestHeader("installedappid") String installedAppId
    );

    @PostMapping("/devices/{controlDeviceId}/command")
    ResponseEntity<JsonNode> executeCommand(
            @PathVariable("controlDeviceId") String controlDeviceId,
            @RequestHeader("installedappid") String installedAppId,
            @RequestBody CommandRequest commandRequest
    );
}
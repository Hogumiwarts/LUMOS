package com.hogumiwarts.lumos.routine.service;

import com.hogumiwarts.lumos.routine.dto.DeviceDto;
import com.hogumiwarts.lumos.routine.entity.Device;
import com.hogumiwarts.lumos.routine.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceDto getDevice(Long deviceId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("기기를 찾을 수 없습니다."));

        return DeviceDto.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .deviceImg(device.getDeviceUrl())
                .control(device.getControl()) // JSON string
                .build();
    }
}

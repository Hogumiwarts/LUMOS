package com.hogumiwarts.lumos.device.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceListResponse {
    private boolean success;
    private List<SmartThingsDevice> devices;
}

package com.hogumiwarts.lumos.routine.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicesSaveRequest {

	private Long deviceId;
	private String installedAppId;
	private String controlId;
	private List<CommandRequest> commands;
}

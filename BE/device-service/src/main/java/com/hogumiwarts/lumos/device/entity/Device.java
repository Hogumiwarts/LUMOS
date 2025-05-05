package com.hogumiwarts.lumos.device.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

	@Id
	private Long deviceId;

	private String installedAppId;

	private Long memberId;

	private String deviceUrl;

	private String controlId;

	private Integer tagNumber;

	private String deviceName;

	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> control;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
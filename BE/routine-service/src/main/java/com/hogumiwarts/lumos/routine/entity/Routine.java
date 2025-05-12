package com.hogumiwarts.lumos.routine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Timestamp;
import java.util.List;

import com.hogumiwarts.lumos.routine.dto.DevicesSaveRequest;

@Entity
@Table(name = "routine", uniqueConstraints = {
	@UniqueConstraint(name = "uq_member_gesture", columnNames = {"member_id", "gesture_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Routine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "routine_id")
	private Long routineId;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "gesture_id", nullable = true)
	private Long gestureId;

	@Column(name = "routine_name")
	private String routineName;

	@Column(name = "routine_icon")
	private String routineIcon;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "devices", columnDefinition = "jsonb")
	private List<DevicesSaveRequest> devices;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Timestamp updatedAt;
}
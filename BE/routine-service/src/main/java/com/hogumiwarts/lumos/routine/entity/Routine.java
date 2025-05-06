package com.hogumiwarts.lumos.routine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "routine")
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

	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "member_gesture_id")
	private Long memberGestureId;

	@Column(name = "routine_icon")
	private Integer routineIcon;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "control", columnDefinition = "jsonb")
	private List<Map<String, Object>> control;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}

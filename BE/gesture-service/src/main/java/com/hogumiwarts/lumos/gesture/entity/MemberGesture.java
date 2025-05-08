package com.hogumiwarts.lumos.gesture.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_gesture")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MemberGesture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_gesture_id")
	private Long memberGestureId;

	@Column(name = "member_id")
	private Long memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gesture_id", insertable = false, updatable = false)
	private Gesture gesture;

	@Column(name = "gesture_id")
	private Long gestureId;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}

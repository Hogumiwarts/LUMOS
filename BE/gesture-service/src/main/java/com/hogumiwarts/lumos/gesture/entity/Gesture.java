package com.hogumiwarts.lumos.gesture.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gesture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gestureId;

	private String gestureName;
	private String gestureImg;
	private Long memberId; // 요청 필터용
}

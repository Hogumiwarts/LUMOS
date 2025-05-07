package com.hogumiwarts.lumos.gesture.service;

import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import com.hogumiwarts.lumos.gesture.entity.Gesture;
import com.hogumiwarts.lumos.gesture.entity.MemberGesture;
import com.hogumiwarts.lumos.gesture.repository.GestureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestureService {

	private final GestureRepository repository;

	// 전체 제스처 목록 조회
	public List<GestureResponse> getGestures(Long memberId) {
		List<MemberGesture> memberGestures = repository.findByMemberId(memberId);

		return memberGestures.stream()
				.map(g -> {
					Gesture gesture = g.getGesture();  // 연관된 Gesture 엔티티
					return GestureResponse.builder()
							.memberGestureId(gesture.getGestureId())
							.gestureName(gesture.getGestureName())
							.gestureImg(gesture.getImageUrl())
							.build();
				})
				.collect(Collectors.toList());
	}

	// 제스처 상세 정보 조회
	@Transactional(readOnly = true)
	public GestureResponse getGestureInfo(Long memberId, Long gestureId) {
		MemberGesture memberGesture = repository.findByMemberIdAndGestureId(memberId, gestureId);
		log.info("조회 요청: gestureId={}, memberId={}", gestureId, memberId);

		if (memberGesture == null) {
			throw new RuntimeException("해당 제스처가 존재하지 않습니다.");
		}

		Gesture gesture = memberGesture.getGesture();  // 연관관계를 통해 접근

		return GestureResponse.builder()
				.memberGestureId(gesture.getGestureId())
				.gestureName(gesture.getGestureName())
				.gestureImg(gesture.getImageUrl())
				.build();
	}
}

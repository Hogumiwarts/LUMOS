package com.hogumiwarts.lumos.gesture.service;

import com.hogumiwarts.lumos.gesture.dto.GestureResponse;
import com.hogumiwarts.lumos.gesture.entity.Gesture;
import com.hogumiwarts.lumos.gesture.repository.GestureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestureService {

	private final GestureRepository repository;

	public List<GestureResponse> getGestures(Long memberId) {
		List<Gesture> gestures = repository.findByMemberId(memberId);

		return gestures.stream()
				.map(g -> GestureResponse.builder()
						.gestureId(g.getGestureId())
						.gestureName(g.getGestureName())
						.gestureImg(g.getGestureImg())
						.build())
				.collect(Collectors.toList());
	}
}

package com.hogumiwarts.lumos.gesture.repository;

import com.hogumiwarts.lumos.gesture.entity.Gesture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestureRepository extends JpaRepository<Gesture, Long> {

	// 특정 memberId로 조회
	List<Gesture> findByMemberId(Long memberId);

}

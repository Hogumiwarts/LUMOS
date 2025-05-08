package com.hogumiwarts.lumos.gesture.repository;

import com.hogumiwarts.lumos.gesture.entity.MemberGesture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GestureRepository extends JpaRepository<MemberGesture, Long> {

	// 특정 memberId로 조회
	List<MemberGesture> findByMemberId(Long memberId);

	Optional<MemberGesture> findByMemberGestureId(Long memberGestureId);

}

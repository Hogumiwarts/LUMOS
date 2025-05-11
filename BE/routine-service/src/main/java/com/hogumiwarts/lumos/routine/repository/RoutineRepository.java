package com.hogumiwarts.lumos.routine.repository;

import java.util.List;
import java.util.Optional;

import com.hogumiwarts.lumos.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByMemberId(Long memberId);
    List<Routine> findByMemberIdAndRoutineId(Long memberId, Long routineId);

    Optional<Routine> findByRoutineIdAndMemberId(Long routineId, Long memberId);

	Optional<Routine> findByMemberIdAndGestureId(Long memberId, Long gestureId);

	@Modifying
	@Query("UPDATE Routine r SET r.gestureId = null WHERE r.memberId = :memberId AND r.gestureId = :gestureId")
	void clearGestureBinding(@Param("memberId") Long memberId, @Param("gestureId") Long gestureId);

	List<Routine> findByMemberIdOrderByRoutineIdAsc(Long memberId);
}
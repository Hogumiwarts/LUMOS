package com.hogumiwarts.lumos.routine.repository;

import java.util.List;
import java.util.Optional;

import com.hogumiwarts.lumos.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByMemberId(Long memberId);
    List<Routine> findByMemberIdAndRoutineId(Long memberId, Long routineId);

    Optional<Routine> findByRoutineIdAndMemberId(Long routineId, Long memberId);

}
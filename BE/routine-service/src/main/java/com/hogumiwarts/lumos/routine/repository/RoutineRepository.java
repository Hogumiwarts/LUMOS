package com.hogumiwarts.lumos.routine.repository;

import java.util.List;

import com.hogumiwarts.lumos.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByMemberId(Long memberId);
}
package com.hogumiwarts.lumos.gesture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hogumiwarts.lumos.gesture.entity.Gesture;

@Repository
public interface GestureRepository extends JpaRepository<Gesture, Long> {
	Optional<Gesture> findByGestureId(Long gestureId);
}

package com.hogumiwarts.lumos.gesturesensor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hogumiwarts.lumos.gesturesensor.entity.GestureSensorData;

@Repository
public interface GestureSensorDataRepository extends JpaRepository<GestureSensorData, Long> {

	// 특정 gestureId로 조회
	List<GestureSensorData> findByGestureId(Integer gestureId);

	// 특정 watchDeviceId로 최근 데이터 조회
	List<GestureSensorData> findTop10ByWatchDeviceIdOrderByCreatedAtDesc(String watchDeviceId);
}

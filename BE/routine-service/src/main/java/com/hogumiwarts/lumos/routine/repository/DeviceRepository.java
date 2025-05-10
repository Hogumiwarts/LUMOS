package com.hogumiwarts.lumos.routine.repository;

import com.hogumiwarts.lumos.routine.entity.Device;
import com.hogumiwarts.lumos.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {


}

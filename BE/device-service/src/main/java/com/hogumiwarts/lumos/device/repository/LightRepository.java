package com.hogumiwarts.lumos.device.repository;

import com.hogumiwarts.lumos.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LightRepository extends JpaRepository<Device, Long> {

}

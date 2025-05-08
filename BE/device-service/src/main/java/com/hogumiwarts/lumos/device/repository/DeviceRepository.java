package com.hogumiwarts.lumos.device.repository;

import com.hogumiwarts.lumos.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
	List<Device> findByMemberId(Long memberId);
	Optional<Device> findByTagNumberAndMemberId(int tagNumber, Long memberId);

    Optional<Object> findByDeviceIdAndMemberId(Long deviceId, Long memberId);

    Optional<Device> findFirstByMemberId(Long memberId);
}

package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, String>, DeviceRepositoryCustom {

    Optional<Device> findByNcId(String ncId);

    Optional<Device> findByDevId(String devId);
}

package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceSearchCondition;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceRepositoryCustom {
    Page<Device> searchByConditions(DeviceSearchCondition condition, Pageable pageable);
}

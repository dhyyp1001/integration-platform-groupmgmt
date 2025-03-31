package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;

import java.util.List;

public interface DeviceGroupRepositoryCustom {
    List<DeviceGroup> findActiveGroups(); // 상태가 "ACTIVE"인 그룹만
}

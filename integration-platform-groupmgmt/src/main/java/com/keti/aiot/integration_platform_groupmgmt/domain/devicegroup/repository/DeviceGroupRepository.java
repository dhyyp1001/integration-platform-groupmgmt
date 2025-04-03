package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long>, DeviceGroupRepositoryCustom {

    Optional<DeviceGroup> findByGroupName(String groupName);

    Page<DeviceGroup> findByGroupId(Long groupId, Pageable pageable);

    Page<DeviceGroup> findByGroupIdContaining(Long groupId, Pageable pageable);

    Page<DeviceGroup> findByGroupNameContainingIgnoreCase(String groupName, Pageable pageable);

}

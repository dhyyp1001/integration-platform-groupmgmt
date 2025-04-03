package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long>, DeviceGroupRepositoryCustom {

    Optional<DeviceGroup> findByGroupName(String groupName);

    Page<DeviceGroup> findByDgpId(Long dgpId, Pageable pageable);

    Page<DeviceGroup> findByGroupIdContainingIgnoreCase(String groupId, Pageable pageable);

    Page<DeviceGroup> findByGroupNameContainingIgnoreCase(String groupName, Pageable pageable);

    // groupId 중 가장 큰 숫자 뒤 3자리 추출
    @Query("SELECT MAX(CAST(SUBSTRING(d.groupId, 4, 3) AS int)) FROM DeviceGroup d WHERE d.groupId LIKE 'grp%'")
    Integer findMaxGroupIdNumber();
}

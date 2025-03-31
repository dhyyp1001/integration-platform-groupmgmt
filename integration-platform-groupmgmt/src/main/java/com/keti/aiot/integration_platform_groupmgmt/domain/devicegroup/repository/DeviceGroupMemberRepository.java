package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceGroupMemberRepository extends JpaRepository<DeviceGroupMember, Long> {

    List<DeviceGroupMember> findByDeviceGroup_GroupId(Long groupId);

    boolean existsByDeviceGroup_GroupIdAndNcId(Long groupId, String ncId);

    boolean existsByDeviceGroup_GroupIdAndDevId(Long groupId, String devId);

    @Modifying
    @Query("DELETE FROM DeviceGroupMember m WHERE m.deviceGroup.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);
}
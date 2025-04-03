package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceSearchCondition;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional(readOnly = true)
    public Page<Device> findDevices(DeviceSearchCondition condition, Pageable pageable) {
        return deviceRepository.searchByConditions(condition, pageable);
    }
}

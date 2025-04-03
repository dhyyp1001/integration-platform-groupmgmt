package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceGroupCreateRequestDto;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.mockito.Mockito.*;

class DeviceGroupServiceTest {

    @InjectMocks
    private DeviceGroupService deviceGroupService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private DeviceRepository deviceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("단말 그룹 생성 테스트")
    void testCreateDeviceGroup() {
        // given
        DeviceGroupCreateRequestDto dto = DeviceGroupCreateRequestDto.builder()
                .groupName("Test Group")
                .description("test group")
                .deviceIds(List.of("NC001", "EDG001"))
                .build();
    }
}
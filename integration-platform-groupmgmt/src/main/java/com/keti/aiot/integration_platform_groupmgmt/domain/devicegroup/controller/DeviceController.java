package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.controller;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceResponseDto;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceSearchCondition;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/search")
    @Operation(summary = "단말 목록조회")
    public ResponseEntity<Page<DeviceResponseDto>> searchDevices(
            @ParameterObject @ModelAttribute DeviceSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(deviceService.findDevices(condition, pageable)
                .map(this::toDto));
    }

    private DeviceResponseDto toDto(Device device) {
        return DeviceResponseDto.builder()
                .devId(device.getDevId())
                .devEui(device.getDevEui())
                .ncId(device.getNcId())
                .description(device.getDescription())
                .channelPlan(device.getChannelPlan())
                .activationType(device.getActivationType())
                .hasMobillity(device.getHasMobillity())
                .priorityNum(device.getPriorityNum())
                .lastSeen(device.getLastSeen())
                .createdAt(device.getCreatedAt())
                .modifiedAt(device.getModifiedAt())
                .build();
    }
}


package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroupResponseDto {
    private Long groupId;
    private String groupName;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DeviceInfoResponseDto> devices;
}
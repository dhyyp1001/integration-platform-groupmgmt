package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceInfoResponseDto {
    private String deviceId;       // dev_id or nc_id
    private String deviceName;     // ex) description
    private String deviceType;     // optional (if applicable)
    private String entityModel;    // optional (if applicable)
    private String location;       // optional (if applicable)
    private String status;         // ex) ONLINE / OFFLINE
}
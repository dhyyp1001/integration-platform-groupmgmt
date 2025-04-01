package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoResponseDto {
    private String type;
    private String devId;
    private String ncId;
    private String devEui;
    private String description;
}

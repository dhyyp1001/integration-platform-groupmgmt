package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroupRequestDto {
    private String groupName;
    private String description;
    //private String status;
}

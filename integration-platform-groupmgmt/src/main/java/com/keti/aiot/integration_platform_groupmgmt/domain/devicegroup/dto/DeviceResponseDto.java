package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponseDto {

    private String devId;
    private String devEui;
    private String ncId;
    private String description;

    private String channelPlan;
    private String activationType;
    private Boolean hasMobillity;

    private Integer priorityNum;

    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

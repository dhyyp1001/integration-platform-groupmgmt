package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfoResponseDto {
    private String type;
    private String devId;
    private String ncId;

    //  단말 상세 정보
    private String devEui;
    private String description;
    private String channelPlan;
    private String activationType;
    private Boolean hasMobillity;
    private Integer priorityNum;
    private LocalDateTime lastSeen;
}

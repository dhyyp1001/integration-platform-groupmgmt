package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_aiot_device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @Column(name = "dev_eui", length = 32)
    private String devEui;

    @Column(name = "dev_id", length = 25)
    private String devId;

    @Column(name = "nc_id", length = 34)
    private String ncId;

    @Column(name = "app_id", length = 34)
    private String appId;

    @Column(name = "app_eui", length = 34)
    private String aapEui;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "channel_plan", length = 12)
    private String channelPlan;

    @Column(name = "activation_type", length = 4)
    private String activationType;

    @Column(name = "has_mobillity")
    private Boolean hasMobillity;

    @Column(name = "priority_num")
    private Integer priorityNum;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "nc_id", length = 34, unique = true)
    private String ncId;

    @Column(name = "dev_id", length = 25, unique = true)
    private String devId;

    @Column(name = "description", length = 4000)
    private String description;
}
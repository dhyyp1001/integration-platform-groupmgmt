package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_aiot_device_group_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "nc_id", length = 34)
    private String ncId;

    @Column(name = "dev_id", length = 25)
    private String devId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dgpId", nullable = false)
    @JsonBackReference //자식에서 부모는 직렬화 불가
    private DeviceGroup deviceGroup;
}

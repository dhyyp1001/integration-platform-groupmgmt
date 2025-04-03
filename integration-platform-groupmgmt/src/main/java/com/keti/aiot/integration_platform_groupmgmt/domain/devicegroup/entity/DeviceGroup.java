package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "device_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dgp_id")
    private Long dgpId;

    @Column(name = "group_id", length = 100, unique = true)
    private String groupId;

    @Column(name = "group_name", length = 100, nullable = false, unique = true)
    private String groupName;

    @Column(columnDefinition = "TEXT")
    private String description;

    //@Column(length = 20)
    //private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "deviceGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference //부모에서 자식은 직렬화
    private List<DeviceGroupMember> members;
}

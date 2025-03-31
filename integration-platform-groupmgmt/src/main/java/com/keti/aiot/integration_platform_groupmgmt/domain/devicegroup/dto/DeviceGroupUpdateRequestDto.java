package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroupUpdateRequestDto {

    @NotBlank
    private String groupName;

    private String description;

    @NotBlank
    private String status;

    @NotBlank
    private String updatedBy;

    @NotEmpty
    private List<String> deviceIds;
}
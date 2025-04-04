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
public class DeviceGroupRequestDto {

    @NotBlank
    private String groupName;

    private String description;

//    @NotBlank
//    private String status;

    @NotEmpty
    private List<String> deviceIds; // dev_id 또는 nc_id (type 기반 매핑)
}
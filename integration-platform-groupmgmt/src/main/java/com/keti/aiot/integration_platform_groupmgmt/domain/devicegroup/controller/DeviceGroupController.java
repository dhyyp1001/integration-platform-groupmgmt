package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.controller;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.*;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service.DeviceGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device-groups")
@RequiredArgsConstructor
@Tag(name = "DeviceGroup", description = "단말 그룹 API")
public class DeviceGroupController {

    private final DeviceGroupService deviceGroupService;

    @PostMapping
    @Operation(summary = "단말 그룹 생성")
    public ResponseEntity<Long> create(@Validated @RequestBody DeviceGroupCreateRequestDto request) {
        return ResponseEntity.ok(deviceGroupService.create(request));
    }

    @PutMapping("/{groupId}")
    @Operation(summary = "단말 그룹 수정")
    public ResponseEntity<Void> update(@PathVariable Long groupId,
                                       @Validated @RequestBody DeviceGroupUpdateRequestDto request) {
        deviceGroupService.update(groupId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "단말 그룹 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long groupId) {
        deviceGroupService.delete(groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "단말 그룹 상세 조회")
    public ResponseEntity<DeviceGroupResponseDto> findById(@PathVariable Long groupId) {
        return ResponseEntity.ok(deviceGroupService.findById(groupId));
    }

    @GetMapping
    @Operation(summary = "단말 그룹 목록 조회")// 평탄화 완료
    public ResponseEntity<Page<DeviceGroupResponseDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(deviceGroupService.findAll(pageable));
    }
}

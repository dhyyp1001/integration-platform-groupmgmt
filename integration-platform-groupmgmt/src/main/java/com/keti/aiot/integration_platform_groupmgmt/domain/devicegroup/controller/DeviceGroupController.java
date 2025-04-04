package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.controller;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.*;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.filter.DeviceGroupSearchFilter;
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

import java.util.List;

@RestController
@RequestMapping("/api/device-groups")
@RequiredArgsConstructor
@Tag(name = "DeviceGroup", description = "단말 그룹 API")
public class DeviceGroupController {

    private final DeviceGroupService deviceGroupService;

    @PostMapping
    @Operation(summary = "단말 그룹 생성")
    public ResponseEntity<String> create(@Validated @RequestBody DeviceGroupRequestDto request) {
        return ResponseEntity.ok(deviceGroupService.create(request));
    }

    @PutMapping("/{dgpId}")
    @Operation(summary = "단말 그룹 수정")
    public ResponseEntity<Void> update(@PathVariable Long dgpId,
                                       @Validated @RequestBody DeviceGroupRequestDto request) {
        deviceGroupService.update(dgpId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{dgpId}")
    @Operation(summary = "단말 그룹 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long dgpId) {
        deviceGroupService.delete(dgpId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "다중 단말 그룹 삭제")
    public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> dgpId) {
        deviceGroupService.deleteAll(dgpId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{dgpId}")
    @Operation(summary = "단말 그룹 상세 조회")
    public ResponseEntity<DeviceGroupResponseDto> findById(@PathVariable Long dgpId) {
        return ResponseEntity.ok(deviceGroupService.findById(dgpId));
    }

    //@GetMapping
    //@Operation(summary = "단말 그룹 목록 조회")// 평탄화 완료
    public ResponseEntity<Page<DeviceGroupResponseDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(deviceGroupService.findAll(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "단말 그룹 목록 검색 조회")
    public ResponseEntity<Page<DeviceGroupResponseDto>> searchDeviceGroups(
            @RequestParam DeviceGroupSearchFilter filter,
            @RequestParam String keyword,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(deviceGroupService.searchDeviceGroups(filter, keyword, pageable));
    }
}

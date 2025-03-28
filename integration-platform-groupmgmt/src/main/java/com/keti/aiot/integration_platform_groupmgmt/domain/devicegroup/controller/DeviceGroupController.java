package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.controller;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceGroupRequestDto;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service.DeviceGroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-groups")
@RequiredArgsConstructor
public class DeviceGroupController {

    private final DeviceGroupService deviceGroupService;

    @Operation(summary = "단말 그룹 전체 조회")
    @GetMapping
    public ResponseEntity<List<DeviceGroup>> findAll() {
        return ResponseEntity.ok(deviceGroupService.findAll());
    }

    @Operation(summary = "단말 그룹 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<DeviceGroup> findById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceGroupService.findById(id));
    }

    @Operation(summary = "단말 그룹 등록")
    @PostMapping
    public ResponseEntity<DeviceGroup> create(@RequestBody DeviceGroupRequestDto dto) {
        return ResponseEntity.ok(deviceGroupService.create(dto));
    }

    @Operation(summary = "단말 그룹 수정")
    @PutMapping("/{id}")
    public ResponseEntity<DeviceGroup> update(@PathVariable Long id, @RequestBody DeviceGroupRequestDto dto) {
        return ResponseEntity.ok(deviceGroupService.update(id, dto));
    }

    @Operation(summary = "단말 그룹 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

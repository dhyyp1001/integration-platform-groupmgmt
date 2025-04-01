package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.*;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroupMember;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupMemberRepository;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupRepository;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final DeviceGroupMemberRepository memberRepository;
    private final DeviceRepository deviceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "device-group-events";

    @Transactional
    public Long create(DeviceGroupCreateRequestDto request) {
        DeviceGroup group = DeviceGroup.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                //.status(request.getStatus())
                .build();

        deviceGroupRepository.save(group);

        List<DeviceGroupMember> members = request.getDeviceIds().stream()
                .map(id -> buildMember(group, id))
                .collect(Collectors.toList());

        memberRepository.saveAll(members);

        sendKafka("CREATE", group);
        return group.getGroupId();
    }

    @Transactional
    public void update(Long groupId, DeviceGroupUpdateRequestDto request) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        //group.setStatus(request.getStatus());

        memberRepository.deleteByGroupId(groupId);

        List<DeviceGroupMember> newMembers = request.getDeviceIds().stream()
                .map(id -> buildMember(group, id))
                .collect(Collectors.toList());

        memberRepository.saveAll(newMembers);

        sendKafka("UPDATE", group);
    }

    @Transactional
    public void delete(Long groupId) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        memberRepository.deleteByGroupId(groupId);
        deviceGroupRepository.delete(group);

        sendKafka("DELETE", group);
    }

    @Transactional(readOnly = true)
    public DeviceGroupResponseDto findById(Long groupId) {
        DeviceGroup group = deviceGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        List<DeviceInfoResponseDto> devices = group.getMembers().stream()
                .map(this::mapToDeviceInfo)
                .collect(Collectors.toList());

        return DeviceGroupResponseDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                //.status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .devices(devices)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<DeviceGroupResponseDto> findAll(Pageable pageable) {// json 평탄화 진행
        return deviceGroupRepository.findAll(pageable)
                .map(group -> {
                    List<DeviceGroupMember> members = memberRepository.findByDeviceGroup_GroupId(group.getGroupId());

                    List<DeviceInfoResponseDto> devices = members.stream()
                            .map(this::mapToDeviceInfo)
                            .collect(Collectors.toList());

                    return DeviceGroupResponseDto.builder()
                            .groupId(group.getGroupId())
                            .groupName(group.getGroupName())
                            .description(group.getDescription())
                            .createdAt(group.getCreatedAt())
                            .updatedAt(group.getUpdatedAt())
                            .devices(devices)
                            .build();
                });
    }

    private DeviceGroupMember buildMember(DeviceGroup group, String deviceId) {
        Optional<Device> device = deviceRepository.findByDevId(deviceId);

        if (device.isPresent()) {
            return DeviceGroupMember.builder()
                    .deviceGroup(group)
                    .type("edge-device")
                    .devId(device.get().getDevId())
                    .build();
        }

        Device ncDevice = deviceRepository.findByNcId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단말입니다."));

        return DeviceGroupMember.builder()
                .deviceGroup(group)
                .type("device")
                .ncId(ncDevice.getNcId())
                .build();
    }

    private DeviceInfoResponseDto mapToDeviceInfo(DeviceGroupMember member) {
        if ("device".equals(member.getType())) {
            Device device = member.getDeviceByNcId();
            return DeviceInfoResponseDto.builder()
                    .type("device")
                    .devId(null)
                    .ncId(member.getNcId())
                    .devEui(device != null ? device.getDevEui() : null)
                    .description(device != null ? device.getDescription() : null)
                    .build();
        } else {
            Device device = member.getDeviceByDevId();
            return DeviceInfoResponseDto.builder()
                    .type("edge-device")
                    .devId(member.getDevId())
                    .ncId(null)//device != null ? device.getNcId() : null)// 수정 필요 시 적용
                    .devEui(device != null ? device.getDevEui() : null)
                    .description(device != null ? device.getDescription() : null)
                    .build();
        }
    }

    private void sendKafka(String eventType, DeviceGroup group) {
        kafkaTemplate.send(TOPIC, eventType + ":" + group.getGroupId());
        log.info("[Kafka] {} 이벤트 전송 완료 - groupId: {}", eventType, group.getGroupId());
    }
}

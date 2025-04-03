package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.*;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroupMember;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.filter.DeviceGroupSearchFilter;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupMemberRepository;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupRepository;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String TOPIC = "aiot.network.management.device-group";

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

    @Transactional
    public void deleteAll(List<Long> groupIds) {

        for (Long groupId : groupIds) {
            DeviceGroup group = deviceGroupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));
            delete(groupId); // 기존 단일 삭제 로직 재사용
            sendKafka("DELETE",group );
        }
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

    @Transactional(readOnly = true)
    public Page<DeviceGroupResponseDto> searchDeviceGroups(DeviceGroupSearchFilter filter, String keyword, Pageable pageable) {
        Page<DeviceGroup> result;

        switch (filter) {
            case groupId -> {
                try {
                    Long groupId = Long.parseLong(keyword); // 완전 일치
                    result = deviceGroupRepository.findByGroupId(groupId, pageable);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("[SEARCH] groupId는 숫자여야 합니다.");
                }
            }

            case groupName -> {
                result = deviceGroupRepository.findByGroupNameContainingIgnoreCase(keyword, pageable); // like
            }

            default -> throw new IllegalArgumentException("[SEARCH] 지원하지 않는 필터입니다.");
        }

        return result.map(group -> {
            List<DeviceInfoResponseDto> devices = memberRepository.findByDeviceGroup_GroupId(group.getGroupId())
                    .stream()
                    .map(this::mapToDeviceInfo)
                    .toList();

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
                    .ncId(device != null ? device.getNcId() : null)// 수정 필요 시 적용
                    .devEui(device != null ? device.getDevEui() : null)
                    .description(device != null ? device.getDescription() : null)
                    .build();
        }
    }

    private void sendKafka(String eventType, DeviceGroup group) {

        try{
            List<DeviceGroupMember> members = memberRepository.findByDeviceGroup_GroupId(group.getGroupId());
            List<DeviceInfoResponseDto> devices = members.stream()
                    .map(this::mapToDeviceInfo)
                    .collect(Collectors.toList());

            Map<String, Object> payload = new HashMap<>();
            payload.put("groupId", group.getGroupId());
            payload.put("groupName", group.getGroupName());
            payload.put("description", group.getDescription());
            payload.put("createdAt", group.getCreatedAt());
            payload.put("updatedAt", group.getUpdatedAt());
            payload.put("members", devices);

            kafkaTemplate.send(
                    MessageBuilder.withPayload(payload)
                            .setHeader(KafkaHeaders.TOPIC, TOPIC)
                            .setHeader("eventType", eventType)
                            .build()
            );

            log.info("[Kafka] {} 메시지 전송 완료 - groupId={}, members={}개", eventType, group.getGroupId(), devices.size());
        } catch (IllegalArgumentException e) {
            log.warn("[Kafka Send Skipped] IllegalArgumentException 발생. groupId={}, message={}",
                    group != null ? group.getGroupId() : "null", e.getMessage());
        }
    }
}

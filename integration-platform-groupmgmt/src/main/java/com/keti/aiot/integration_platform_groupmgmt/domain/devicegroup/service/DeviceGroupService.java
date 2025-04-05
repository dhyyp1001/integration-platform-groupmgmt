package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.*;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroupMember;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.filter.DeviceGroupSearchFilter;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupMemberRepository;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupRepository;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceRepository;
import com.keti.aiot.integration_platform_groupmgmt.global.exception.NotFoundException;
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
    public String create(DeviceGroupRequestDto request) {

        // 가장 마지막 번호 가져오기
        Integer lastNumber = deviceGroupRepository.findMaxGroupIdNumber();
        int nextNumber = (lastNumber == null) ? 1 : lastNumber + 1;

        // 숫자를 세 자리 문자열로 포맷팅
        String nextGroupId = String.format("grp%03d", nextNumber);

        DeviceGroup group = DeviceGroup.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .groupId(nextGroupId)
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
    public void update(Long dgpId, DeviceGroupRequestDto request) {
        DeviceGroup group = deviceGroupRepository.findById(dgpId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 단말 그룹이 존재하지 않습니다."));

        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        //group.setStatus(request.getStatus());

        memberRepository.deleteByDgpId(dgpId);

        List<DeviceGroupMember> newMembers = request.getDeviceIds().stream()
                .map(id -> buildMember(group, id))
                .collect(Collectors.toList());

        memberRepository.saveAll(newMembers);

        sendKafka("UPDATE", group);
    }

    @Transactional
    public void delete(Long dgpId) {
        DeviceGroup group = deviceGroupRepository.findById(dgpId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 단말 그룹이 존재하지 않습니다."));

        memberRepository.deleteByDgpId(dgpId);
        deviceGroupRepository.delete(group);

        sendKafka("DELETE", group);
    }

    @Transactional
    public void deleteAll(List<Long> dgpIds) {

        for (Long dgpId : dgpIds) {
            DeviceGroup group = deviceGroupRepository.findById(dgpId)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 단말 그룹이 있습니다."));
            delete(dgpId); // 기존 단일 삭제 로직 재사용
            sendKafka("DELETE",group );
        }
    }

    @Transactional(readOnly = true)
    public DeviceGroupResponseDto findById(Long dgpId) {
        DeviceGroup group = deviceGroupRepository.findById(dgpId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 단말 그룹이 존재하지 않습니다."));

        List<DeviceInfoResponseDto> devices = group.getMembers().stream()
                .map(this::mapToDeviceInfo)
                .collect(Collectors.toList());

        return DeviceGroupResponseDto.builder()
                .dgpId(group.getDgpId())
                .groupName(group.getGroupName())
                .groupId(group.getGroupId())
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
                    List<DeviceGroupMember> members = memberRepository.findByDeviceGroup_DgpId(group.getDgpId());

                    List<DeviceInfoResponseDto> devices = members.stream()
                            .map(this::mapToDeviceInfo)
                            .collect(Collectors.toList());

                    return DeviceGroupResponseDto.builder()
                            .dgpId(group.getDgpId())
                            .groupName(group.getGroupName())
                            .groupId(group.getGroupId())
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
                result = deviceGroupRepository.findByGroupIdContainingIgnoreCase(keyword, pageable);
            }

            case groupName -> {
                result = deviceGroupRepository.findByGroupNameContainingIgnoreCase(keyword, pageable); // like
            }

            default -> throw new NotFoundException("[SEARCH] 지원하지 않는 필터입니다.");
        }

        log.info("검색 필터: {}, 키워드: {}", filter, keyword);

        return result.map(group -> {
            List<DeviceInfoResponseDto> devices = memberRepository.findByDeviceGroup_DgpId(group.getDgpId())
                    .stream()
                    .map(this::mapToDeviceInfo)
                    .toList();

            return DeviceGroupResponseDto.builder()
                    .dgpId(group.getDgpId())
                    .groupName(group.getGroupName())
                    .groupId(group.getGroupId())
                    .description(group.getDescription())
                    .createdAt(group.getCreatedAt())
                    .updatedAt(group.getUpdatedAt())
                    .devices(devices)
                    .build();
        });
    }


    private DeviceGroupMember buildMember(DeviceGroup group, String deviceId) {
        Device device = deviceRepository.findByDevId(deviceId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 단말입니다."));

        if (device.getNcId() == null) {
            return DeviceGroupMember.builder()
                    .deviceGroup(group)
                    .type("edge-device")
                    .devId(device.getDevId())
                    .build();
        }

        return DeviceGroupMember.builder()
                .deviceGroup(group)
                .type("device")
                .devId(device.getDevId())
                .ncId(device.getNcId())
                .build();
    }

    private DeviceInfoResponseDto mapToDeviceInfo(DeviceGroupMember member) {
        if ("device".equals(member.getType())) {
            Device device = (deviceRepository.findByNcId(member.getNcId()))
                    .orElseThrow(() -> new RuntimeException("Device not found"));;
            return DeviceInfoResponseDto.builder()
                    .type("device")
                    .devId(null)
                    .ncId(member.getNcId())
                    .devEui(device != null ? device.getDevEui() : null)
                    .description(device != null ? device.getDescription() : null)
                    .build();
        } else {
            Device device = deviceRepository.findByDevId(member.getDevId())
                    .orElseThrow(() -> new RuntimeException("Device not found"));
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
            List<DeviceGroupMember> members = memberRepository.findByDeviceGroup_DgpId(group.getDgpId());
            List<DeviceInfoResponseDto> devices = members.stream()
                    .map(this::mapToDeviceInfo)
                    .collect(Collectors.toList());

            Map<String, Object> payload = new HashMap<>();
            payload.put("dgpId", group.getDgpId());
            payload.put("groupName", group.getGroupName());
            payload.put("groupId", group.getGroupId());
            payload.put("description", group.getDescription());
            payload.put("members", devices);

            kafkaTemplate.send(
                    MessageBuilder.withPayload(payload)
                            .setHeader(KafkaHeaders.TOPIC, TOPIC)
                            .setHeader("eventType", eventType)
                            .build()
            );

            log.info("[Kafka] {} 메시지 전송 완료 - dgpId={}, members={}개", eventType, group.getDgpId(), devices.size());
        } catch (IllegalArgumentException e) {
            log.warn("[Kafka Send Skipped] IllegalArgumentException 발생. dgpId={}, message={}",
                    group != null ? group.getDgpId() : "null", e.getMessage());
        } catch (NotFoundException e){
            log.warn("[Kafka Send Skipped] NotFoundException 발생. dgpId={}, message={}",
                    group != null ? group.getDgpId() : "null", e.getMessage());
        } catch (Exception e){
            log.warn("exception kafka send skipped");
        }
    }
}

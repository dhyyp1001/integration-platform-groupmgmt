package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceGroupRequestDto;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.kafka.DeviceGroupProducer;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository.DeviceGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final DeviceGroupProducer deviceGroupProducer;
    private final ObjectMapper objectMapper; // Kafka JSON 변환용, di 주입

    /**
     * 단말 그룹 전체 조회
     */
    public List<DeviceGroup> findAll() {
        return deviceGroupRepository.findAll();
    }

    /**
     * 단말 그룹 단건 조회
     */
    public DeviceGroup findById(Long id) {
        return deviceGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("단말 그룹을 찾을 수 없습니다. id=" + id));
    }

    /**
     * 단말 그룹 등록
     */
    public DeviceGroup create(DeviceGroupRequestDto dto) {
        DeviceGroup saved = deviceGroupRepository.save(DeviceGroup.builder()
                .groupName(dto.getGroupName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build());

        sendKafka("CREATE", saved);
        return saved;
    }

    /**
     * 단말 그룹 수정
     */
    public DeviceGroup update(Long id, DeviceGroupRequestDto dto) {
        DeviceGroup group = deviceGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 단말 그룹을 찾을 수 없습니다. id=" + id));

        group.setGroupName(dto.getGroupName());
        group.setDescription(dto.getDescription());
        group.setStatus(dto.getStatus());

        DeviceGroup updated = deviceGroupRepository.save(group);
        sendKafka("UPDATE", updated);
        return updated;
    }

    /**
     * 단말 그룹 삭제
     */
    public void delete(Long id) {
        DeviceGroup group = deviceGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 단말 그룹을 찾을 수 없습니다. id=" + id));

        deviceGroupRepository.delete(group);
        sendKafka("DELETE", group);
    }

    /**
     * Kafka 메시지 전송
     */
    private void sendKafka(String eventType, DeviceGroup group) {
        try {
            String payload = objectMapper.writeValueAsString(group);
            deviceGroupProducer.send(eventType, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }
}

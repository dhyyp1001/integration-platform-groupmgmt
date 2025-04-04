/*package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceGroupProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "device-group-events";

    public void send(String eventType, String message) {
        String payload = String.format("{\"event\":\"%s\", \"data\":%s}", eventType, message);
        kafkaTemplate.send(TOPIC, payload);
        log.info("[Kafka Produce] {} → {}", eventType, payload);
    }
}*/

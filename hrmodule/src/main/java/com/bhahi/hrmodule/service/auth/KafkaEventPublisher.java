package com.bhahi.hrmodule.service.auth;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.OTP.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

/**
 * Publishes any event DTO to the hr-events topic as JSON. The event type goes in a header,
 * so the same publisher is used for every purpose (OTP mail now, other classes later).
 * It never waits for Kafka or for the receiver.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public void publish(KafkaEventType type, String key, Object event) {
        publish(type, key, event, () -> { });
    }

    /**
     * @param key       Kafka message key, events with the same key keep their order (use the login id)
     * @param onFailure runs if the event could not be published
     */
    public void publish(KafkaEventType type, String key, Object event, Runnable onFailure) {
        try {
            String payload = jsonMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(KafkaTopics.HR_EVENTS, key, payload);
            record.headers().add(KafkaTopics.EVENT_TYPE_HEADER, type.name().getBytes(StandardCharsets.UTF_8));

            kafkaTemplate.send(record).whenComplete((result, ex) -> {
                if (ex != null) {
                    // The payload is never logged, it can contain an OTP.
                    log.error("Could not publish {} event, key={}", type, key, ex);
                    onFailure.run();
                }
            });
        } catch (Exception e) {
            log.error("Could not publish {} event, key={}", type, key, e);
            onFailure.run();
        }
    }
}
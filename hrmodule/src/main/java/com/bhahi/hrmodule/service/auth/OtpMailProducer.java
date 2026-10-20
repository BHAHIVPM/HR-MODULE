package com.bhahi.hrmodule.service.auth;


import com.bhahi.hr.dto.OTP.OtpMailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpMailProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    @Value("${app.kafka.otp-mail-topic}")
    private String topic;

    /**
     * Publishes the event and returns immediately, it does not wait for Kafka or for the mail.
     * If publishing fails, onFailure runs (used to discard the OTP, since no mail will go out).
     * The OTP itself is never logged.
     */
    public void publish(OtpMailEvent event, Runnable onFailure) {
        try {
            String payload = jsonMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.loginId(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Could not publish OTP mail event for loginId={}", event.loginId(), ex);
                            onFailure.run();
                        }
                    });
        } catch (Exception e) {
            log.error("Could not publish OTP mail event for loginId={}", event.loginId(), e);
            onFailure.run();
        }
    }
}
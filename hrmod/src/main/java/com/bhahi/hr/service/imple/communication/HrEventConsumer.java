package com.bhahi.hr.service.imple.communication;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.OTP.KafkaTopics;
import com.bhahi.hr.dto.OTP.OtpMailEvent;
import com.bhahi.hr.service.inter.communication.IMailMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

/**
 * Receives every event hrmodule sends and hands it to the right handler by event type.
 * To support a new purpose, add a case in onMessage and a handler method.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HrEventConsumer {

    private final IMailMethods mailMethods;
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = KafkaTopics.HR_EVENTS)
    public void onMessage(ConsumerRecord<String, String> record) {
        KafkaEventType type = readType(record);
        if (type == null) {
            return;
        }
        try {
            switch (type) {
                case OTP_MAIL -> handleOtpMail(record);
            }
        } catch (Exception e) {
            // The payload is never logged, it can contain an OTP.
            log.error("Could not process {} event, key={}", type, record.key(), e);
        }
    }

    private void handleOtpMail(ConsumerRecord<String, String> record) {
        OtpMailEvent event = jsonMapper.readValue(record.value(), OtpMailEvent.class);

        // If this server was down for a while, the OTP may have expired already. Do not mail a dead OTP.
        long ageMillis = System.currentTimeMillis() - record.timestamp();
        if (ageMillis > event.validMinutes() * 60_000L) {
            log.warn("Skipped an expired OTP mail for loginId={}", event.loginId());
            return;
        }

        boolean sent = mailMethods.otpMail(event.email(), event.userName(), event.otp(), event.validMinutes());
        if (!sent) {
            log.error("OTP mail could not be sent for loginId={}", event.loginId());
        }
    }

    // Returns null (and skips the message) if the header is missing or the type is unknown,
    // so an older hrmod does not break when hrmodule starts sending a newer event type.
    private KafkaEventType readType(ConsumerRecord<String, String> record) {
        Header header = record.headers().lastHeader(KafkaTopics.EVENT_TYPE_HEADER);
        if (header == null) {
            log.warn("Skipped a message without an event type, key={}", record.key());
            return null;
        }
        String name = new String(header.value(), StandardCharsets.UTF_8);
        try {
            return KafkaEventType.valueOf(name);
        } catch (IllegalArgumentException e) {
            log.warn("Skipped a message with unknown event type '{}'", name);
            return null;
        }
    }
}
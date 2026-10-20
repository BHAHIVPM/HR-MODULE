package com.bhahi.hr.service.imple.communication;


import com.bhahi.hr.dto.OTP.OtpMailEvent;
import com.bhahi.hr.service.inter.communication.IMailMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lives in the mail (receiver) project. Reads OTP mail events published by the
 * user login (sender) project and sends the email.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpMailConsumer {

    private final IMailMethods mailMethods;
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = "${app.kafka.otp-mail-topic}")
    public void onMessage(String payload) {
        OtpMailEvent event = jsonMapper.readValue(payload, OtpMailEvent.class);
        boolean sent = mailMethods.otpMail(event.email(), event.userName(), event.otp(), event.validMinutes());
        if (!sent) {
            log.error("OTP mail could not be sent for loginId={}", event.loginId());
        }
    }
}
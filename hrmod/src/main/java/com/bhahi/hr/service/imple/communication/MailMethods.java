package com.bhahi.hr.service.imple.communication;

import com.bhahi.hr.service.inter.communication.IMailMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailMethods implements IMailMethods {

    private final MailService mailService;
    private final TemplateForEmail templateForEmail;

    private static final String SENDER_NAME = "HR Module";

    // Client accent colour used for the registration email header. Kept static for
    // now; pass a dynamic per-client value later if required.
    private static final String THEME_COLOR = "#0d6efd";

    @Async("mailExecutor")
    @Override
    public void accountCreatedMail(String email, String userName, String loginId, String tempPassword) {
        // Silently skip rather than fail the registration flow if the email is missing.
        if (email == null || email.isBlank()) {
            return;
        }
        mailService.sendEmail(
                SENDER_NAME,
                email,
                "Account Created - Login Credentials",
                templateForEmail.accountRegistration(userName, loginId, tempPassword, THEME_COLOR),
                false,
                "");
    }

    // Not @Async on purpose: the OTP flow needs to know whether the mail was sent,
    // so it can discard the OTP and tell the user if sending fails.
    @Override
    public boolean otpMail(String email, String userName, String otp, int validMinutes) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return mailService.sendEmail(
                SENDER_NAME,
                email,
                "OTP Verification",
                templateForEmail.otpMail(userName, otp, validMinutes, THEME_COLOR),
                false,
                "");
    }
}
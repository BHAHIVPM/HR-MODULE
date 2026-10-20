package com.bhahi.hr.dto.OTP;

/**
 * Shared by hrmodule (publishes it) and hrmod (consumes it).
 * It is sent through Kafka as JSON, so the field names are the JSON keys.
 */
public record OtpMailEvent(
        String loginId,
        String email,
        String userName,
        String otp,
        int validMinutes
) {
}
package com.bhahi.hr.service.inter.communication;

public interface IMailMethods {

    /**
     * Sends the registration confirmation mail (login id + temporary password)
     * to a newly created user, asynchronously on the {@code mailExecutor}.
     */
    void accountCreatedMail(String email, String userName, String loginId, String tempPassword);

    /**
     * Sends the OTP verification mail. Runs synchronously so the caller knows
     * whether the mail was sent.
     *
     * @return true if the mail was sent, false if the email is missing or sending failed
     */
    boolean otpMail(String email, String userName, String otp, int validMinutes);
}
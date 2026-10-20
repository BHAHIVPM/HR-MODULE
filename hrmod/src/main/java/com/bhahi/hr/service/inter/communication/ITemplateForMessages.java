package com.bhahi.hr.service.inter.communication;

public interface ITemplateForMessages {

    /**
     * Builds the HTML body for a newly registered account, containing the
     * user's login id and temporary password.
     *
     * @param userName    display name of the user
     * @param loginId     generated login id for the account
     * @param tempPassword generated temporary password
     * @param themeColor  accent (client) color applied to the header; keep as a
     *                    static variable for now and pass a dynamic client value later
     * @return the rendered HTML string
     */
    String accountRegistration(String userName, String loginId, String tempPassword, String themeColor);

    /**
     * Builds the HTML body for the OTP verification mail.
     *
     * @param userName     display name of the user (falls back to "Sir/Madam" if blank)
     * @param otp          the one-time password to show
     * @param validMinutes how long the OTP stays valid, shown in the mail
     * @param themeColor   accent color applied to the header and OTP digits
     * @return the rendered HTML string
     */
    String otpMail(String userName, String otp, int validMinutes, String themeColor);
}
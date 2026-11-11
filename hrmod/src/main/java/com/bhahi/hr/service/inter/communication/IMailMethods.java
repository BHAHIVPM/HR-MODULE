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

    /**
     * Sends candidate status/recruitment notification emails asynchronously.
     */
    void candidateNotificationMail(String email, String candidateName, String jobTitle, String stage, String templateType);

    /**
     * Sends expense approval/reimbursement notification emails asynchronously.
     */
    void expenseApprovalNotificationMail(String managerEmail, Long claimId, Integer employeeId, java.math.BigDecimal amount, String currency, String status, String description);

    /**
     * Sends training assignment and reminder emails asynchronously.
     */
    void trainingAssignmentNotificationMail(String employeeEmail, Long enrollmentId, Integer employeeId, String courseName, java.time.LocalDate dueDate, String reminderType);

    /**
     * Sends benefit enrollment confirmation emails asynchronously.
     */
    void benefitEnrollmentNotificationMail(String employeeEmail, Long enrollmentId, Integer employeeId, String planType, String planName, java.time.LocalDate effectiveDate, String status);

    /**
     * Sends exit clearance and resignation notification emails asynchronously.
     */
    void exitClearanceNotificationMail(String employeeEmail, Long exitId, Integer employeeId, java.time.LocalDate resignationDate, java.time.LocalDate lastWorkingDay, String clearanceDepartment, String status);

    /**
     * Sends helpdesk ticket confirmation and update emails asynchronously.
     */
    void helpdeskTicketNotificationMail(String creatorEmail, Long ticketId, Integer employeeId, String category, String subject, String priority, String status);

    /**
     * Sends company announcement and culture broadcast emails asynchronously.
     */
    void cultureBroadcastNotificationMail(String announcementId, String title, String targetDepartment, String messageType, String authorEmail, java.time.LocalDate broadcastDate);
}
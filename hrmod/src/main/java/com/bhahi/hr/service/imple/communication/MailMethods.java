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

    @Async("mailExecutor")
    @Override
    public void candidateNotificationMail(String email, String candidateName, String jobTitle, String stage, String templateType) {
        if (email == null || email.isBlank()) {
            return;
        }
        String subject = switch (templateType) {
            case "CANDIDATE_SHORTLISTED" -> "Update: Shortlisted for " + jobTitle;
            case "INTERVIEW_INVITE" -> "Interview Invitation for " + jobTitle;
            case "OFFER_LETTER" -> "Job Offer: " + jobTitle;
            case "CANDIDATE_REJECTED" -> "Update on your application for " + jobTitle;
            case "WELCOME_ABOARD" -> "Welcome to the team - " + jobTitle;
            default -> "Application Update: " + jobTitle;
        };

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Hello " + candidateName + ",</h2>" +
                "<p>We have an update regarding your application for the position of <strong>" + jobTitle + "</strong>.</p>" +
                "<p>Current Stage: <strong>" + stage + "</strong></p>" +
                "<p>Thank you for your interest in joining us.</p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, email, subject, body, false, "");
    }

    @Async("mailExecutor")
    @Override
    public void expenseApprovalNotificationMail(String managerEmail, Long claimId, Integer employeeId, java.math.BigDecimal amount, String currency, String status, String description) {
        String recipient = (managerEmail != null && !managerEmail.isBlank()) ? managerEmail : "finance@company.com";
        String subject = "Expense Claim #" + claimId + " - Status: " + status;

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Expense Claim Update</h2>" +
                "<p>Claim ID: <strong>#" + claimId + "</strong></p>" +
                "<p>Employee ID: <strong>" + employeeId + "</strong></p>" +
                "<p>Amount: <strong>" + amount + " " + currency + "</strong></p>" +
                "<p>Status: <strong style=\"color: #0d6efd;\">" + status + "</strong></p>" +
                "<p>Description: " + (description != null ? description : "N/A") + "</p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, recipient, subject, body, false, "");
    }

    @Async("mailExecutor")
    @Override
    public void trainingAssignmentNotificationMail(String employeeEmail, Long enrollmentId, Integer employeeId, String courseName, java.time.LocalDate dueDate, String reminderType) {
        if (employeeEmail == null || employeeEmail.isBlank()) {
            return;
        }
        String subject = "New Training Assigned: " + courseName;

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Training Course Assignment</h2>" +
                "<p>You have been assigned to course: <strong>" + courseName + "</strong></p>" +
                "<p>Enrollment ID: <strong>#" + enrollmentId + "</strong></p>" +
                "<p>Due Date: <strong>" + (dueDate != null ? dueDate.toString() : "No due date") + "</strong></p>" +
                "<p>Please complete this course before the deadline.</p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, employeeEmail, subject, body, false, "");
    }

    @Async("mailExecutor")
    @Override
    public void benefitEnrollmentNotificationMail(String employeeEmail, Long enrollmentId, Integer employeeId, String planType, String planName, java.time.LocalDate effectiveDate, String status) {
        if (employeeEmail == null || employeeEmail.isBlank()) {
            return;
        }
        String subject = "Benefit Enrollment Confirmation - " + planName;

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Benefit Enrollment Confirmation</h2>" +
                "<p>Plan Type: <strong>" + planType + "</strong></p>" +
                "<p>Plan Name: <strong>" + planName + "</strong></p>" +
                "<p>Enrollment ID: <strong>#" + enrollmentId + "</strong></p>" +
                "<p>Effective Date: <strong>" + (effectiveDate != null ? effectiveDate.toString() : "Immediate") + "</strong></p>" +
                "<p>Status: <strong style=\"color: #198754;\">" + status + "</strong></p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, employeeEmail, subject, body, false, "");
    }

    @Async("mailExecutor")
    @Override
    public void exitClearanceNotificationMail(String employeeEmail, Long exitId, Integer employeeId, java.time.LocalDate resignationDate, java.time.LocalDate lastWorkingDay, String clearanceDepartment, String status) {
        if (employeeEmail == null || employeeEmail.isBlank()) {
            return;
        }
        String subject = "Resignation & Exit Clearance Process - #" + exitId;

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Resignation & Clearance Notification</h2>" +
                "<p>Exit ID: <strong>#" + exitId + "</strong></p>" +
                "<p>Resignation Date: <strong>" + (resignationDate != null ? resignationDate.toString() : "N/A") + "</strong></p>" +
                "<p>Last Working Day: <strong>" + (lastWorkingDay != null ? lastWorkingDay.toString() : "TBD") + "</strong></p>" +
                "<p>Department Clearance: <strong>" + clearanceDepartment + "</strong></p>" +
                "<p>Status: <strong style=\"color: #dc3545;\">" + status + "</strong></p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, employeeEmail, subject, body, false, "");
    }

    @Async("mailExecutor")
    @Override
    public void helpdeskTicketNotificationMail(String creatorEmail, Long ticketId, Integer employeeId, String category, String subjectText, String priority, String status) {
        String recipient = (creatorEmail != null && !creatorEmail.isBlank()) ? creatorEmail : "support@company.com";
        String subject = "Helpdesk Ticket #" + ticketId + " [" + category + "] - " + status;

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Helpdesk Ticket Update</h2>" +
                "<p>Ticket ID: <strong>#" + ticketId + "</strong></p>" +
                "<p>Category: <strong>" + category + "</strong></p>" +
                "<p>Subject: <strong>" + subjectText + "</strong></p>" +
                "<p>Priority: <strong>" + priority + "</strong></p>" +
                "<p>Status: <strong style=\"color: #0d6efd;\">" + status + "</strong></p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, recipient, subject, body, false, "");
    }

    @Async("mailExecutor")
    @Override
    public void cultureBroadcastNotificationMail(String announcementId, String title, String targetDepartment, String messageType, String authorEmail, java.time.LocalDate broadcastDate) {
        String recipient = (authorEmail != null && !authorEmail.isBlank()) ? authorEmail : "all-employees@company.com";
        String subject = "Company Announcement [" + messageType + "]: " + title;

        String body = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Company Announcement</h2>" +
                "<p>Title: <strong>" + title + "</strong></p>" +
                "<p>Target Department: <strong>" + targetDepartment + "</strong></p>" +
                "<p>Type: <strong>" + messageType + "</strong></p>" +
                "<p>Broadcast Date: <strong>" + (broadcastDate != null ? broadcastDate.toString() : "Today") + "</strong></p>" +
                "<br/><p>Best regards,<br/><strong>" + SENDER_NAME + "</strong></p>" +
                "</div>";

        mailService.sendEmail(SENDER_NAME, recipient, subject, body, false, "");
    }
}
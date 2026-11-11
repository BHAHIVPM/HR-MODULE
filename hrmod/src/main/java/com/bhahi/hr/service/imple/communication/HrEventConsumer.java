package com.bhahi.hr.service.imple.communication;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.OTP.KafkaTopics;
import com.bhahi.hr.dto.OTP.OtpMailEvent;
import com.bhahi.hr.dto.benefits.BenefitEnrollmentEvent;
import com.bhahi.hr.dto.culture.CultureBroadcastEvent;
import com.bhahi.hr.dto.exit.ExitClearanceEvent;
import com.bhahi.hr.dto.expense.ExpenseApprovalEvent;
import com.bhahi.hr.dto.helpdesk.HelpdeskTicketEvent;
import com.bhahi.hr.dto.recruitment.CandidateNotificationEvent;
import com.bhahi.hr.dto.training.TrainingAssignmentEvent;
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
                case CANDIDATE_NOTIFICATION -> handleCandidateNotification(record);
                case EXPENSE_APPROVAL -> handleExpenseApproval(record);
                case TRAINING_ASSIGNMENT -> handleTrainingAssignment(record);
                case BENEFIT_ENROLLMENT -> handleBenefitEnrollment(record);
                case EXIT_CLEARANCE -> handleExitClearance(record);
                case HELPDESK_TICKET -> handleHelpdeskTicket(record);
                case CULTURE_BROADCAST -> handleCultureBroadcast(record);
            }
        } catch (Exception e) {
            // The payload is never logged, it can contain an OTP.
            log.error("Could not process {} event, key={}", type, record.key(), e);
        }
    }

    private void handleCultureBroadcast(ConsumerRecord<String, String> record) {
        CultureBroadcastEvent event = jsonMapper.readValue(record.value(), CultureBroadcastEvent.class);
        mailMethods.cultureBroadcastNotificationMail(
                event.announcementId() != null ? event.announcementId().toString() : "0",
                event.title(),
                event.targetDepartment(),
                event.messageType(),
                event.authorEmail(),
                event.broadcastDate()
        );
        log.info("Processed culture broadcast notification for announcementId={}, title={}", event.announcementId(), event.title());
    }

    private void handleHelpdeskTicket(ConsumerRecord<String, String> record) {
        HelpdeskTicketEvent event = jsonMapper.readValue(record.value(), HelpdeskTicketEvent.class);
        mailMethods.helpdeskTicketNotificationMail(
                event.creatorEmail(),
                event.ticketId(),
                event.employeeId(),
                event.category(),
                event.subject(),
                event.priority(),
                event.status()
        );
        log.info("Processed helpdesk ticket notification for ticketId={}, category={}", event.ticketId(), event.category());
    }

    private void handleExitClearance(ConsumerRecord<String, String> record) {
        ExitClearanceEvent event = jsonMapper.readValue(record.value(), ExitClearanceEvent.class);
        mailMethods.exitClearanceNotificationMail(
                event.employeeEmail(),
                event.exitId(),
                event.employeeId(),
                event.resignationDate(),
                event.lastWorkingDay(),
                event.clearanceDepartment(),
                event.status()
        );
        log.info("Processed exit clearance notification for exitId={}, employeeId={}", event.exitId(), event.employeeId());
    }

    private void handleBenefitEnrollment(ConsumerRecord<String, String> record) {
        BenefitEnrollmentEvent event = jsonMapper.readValue(record.value(), BenefitEnrollmentEvent.class);
        mailMethods.benefitEnrollmentNotificationMail(
                event.employeeEmail(),
                event.enrollmentId(),
                event.employeeId(),
                event.planType(),
                event.planName(),
                event.effectiveDate(),
                event.status()
        );
        log.info("Processed benefit enrollment notification for enrollmentId={}, planName={}", event.enrollmentId(), event.planName());
    }

    private void handleTrainingAssignment(ConsumerRecord<String, String> record) {
        TrainingAssignmentEvent event = jsonMapper.readValue(record.value(), TrainingAssignmentEvent.class);
        mailMethods.trainingAssignmentNotificationMail(
                event.employeeEmail(),
                event.enrollmentId(),
                event.employeeId(),
                event.courseName(),
                event.dueDate(),
                event.reminderType()
        );
        log.info("Processed training assignment notification for enrollmentId={}, courseId={}", event.enrollmentId(), event.courseId());
    }

    private void handleExpenseApproval(ConsumerRecord<String, String> record) {
        ExpenseApprovalEvent event = jsonMapper.readValue(record.value(), ExpenseApprovalEvent.class);
        mailMethods.expenseApprovalNotificationMail(
                event.managerEmail(),
                event.claimId(),
                event.employeeId(),
                event.amount(),
                event.currency(),
                event.status(),
                event.description()
        );
        log.info("Processed expense approval notification for claimId={}, status={}", event.claimId(), event.status());
    }

    private void handleCandidateNotification(ConsumerRecord<String, String> record) {
        CandidateNotificationEvent event = jsonMapper.readValue(record.value(), CandidateNotificationEvent.class);
        mailMethods.candidateNotificationMail(
                event.email(),
                event.candidateName(),
                event.jobTitle(),
                event.stage(),
                event.templateType()
        );
        log.info("Processed candidate notification for candidateId={}, stage={}", event.candidateId(), event.stage());
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
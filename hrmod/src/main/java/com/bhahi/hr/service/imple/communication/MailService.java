package com.bhahi.hr.service.imple.communication;

import com.bhahi.hr.service.inter.communication.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    // ============================================================================
    // Sends an email to a specified recipient with optional HTML content and
    // attachment. The "from" address comes from the existing mail configuration
    // (spring.mail.username) while senderName is used as the display name.
    // Includes validation for attachment path and file existence before adding.
    // Logs success and failure for tracking.
    // ============================================================================
    @Override
    public boolean sendEmail(
            String senderName,
            String recipient,
            String subject,
            String htmlContent,
            boolean hasAttachment,
            @Nullable
            String attachmentPath
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, hasAttachment, "UTF-8");

            messageHelper.setFrom(from, senderName);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(htmlContent, true);

            if (hasAttachment && attachmentPath != null) {

                File attachmentFile = new File(attachmentPath);
                if (attachmentFile.exists() && attachmentFile.isFile()) {
                    FileSystemResource file = new FileSystemResource(attachmentFile);
                    String fileName = file.getFilename();
                    if (fileName != null && !fileName.isEmpty()) {

                        messageHelper.addAttachment(fileName, file);
                    }
                    logger.debug("Attachment added: {}", attachmentFile.getName());
                } else {
                    logger.warn("Attachment file not found or is not a valid file: {}", attachmentPath);
                }
            }

            mailSender.send(message);
            logger.info("Email successfully sent to {} with subject: {}", recipient, subject);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to send email to {} with subject: {}", recipient, subject, e);
            return false;
        }
    }
}

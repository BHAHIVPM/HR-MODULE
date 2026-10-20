package com.bhahi.hr.service.inter.communication;

import org.springframework.lang.Nullable;

public interface IMailService {

    boolean sendEmail(
            String senderName,
            String recipient,
            String subject,
            String htmlContent,
            boolean hasAttachment,
            @Nullable String attachmentPath
    );
}

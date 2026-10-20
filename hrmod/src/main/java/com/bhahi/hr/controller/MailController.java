package com.bhahi.hr.controller;

import com.bhahi.hr.service.imple.communication.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mailing-api")
public class MailController {

    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-mail")
    public String test() {
        emailService.sendSimpleMail("bhahivpm@gmail.com", "Test", "It works!");
        return "sent";
    }
//    @Scheduled(cron = "0 0 9 * * *") // every day at 9 AM
//    public void sendDailyMail() {
//        emailService.sendSimpleMail("user@example.com", "Daily report", "Hello!");
//    }
}

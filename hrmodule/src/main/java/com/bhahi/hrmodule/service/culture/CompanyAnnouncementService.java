package com.bhahi.hrmodule.service.culture;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.culture.CultureBroadcastEvent;
import com.bhahi.hrmodule.model.culture.CompanyAnnouncement;
import com.bhahi.hrmodule.repository.culture.CompanyAnnouncementRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyAnnouncementService {

    private final CompanyAnnouncementRepository announcementRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<CompanyAnnouncement> broadcast(CompanyAnnouncement announcement) {
        ResponseMessage<CompanyAnnouncement> response = new ResponseMessage<>();
        try {
            if (announcement.getBroadcastDate() == null) {
                announcement.setBroadcastDate(LocalDate.now());
            }
            if (announcement.getTargetDepartment() == null) {
                announcement.setTargetDepartment("ALL");
            }
            CompanyAnnouncement saved = announcementRepository.save(announcement);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Company announcement broadcasted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Broadcast failed");
            response.setMessage("Could not broadcast announcement. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<CompanyAnnouncement>> findAll() {
        ResponseMessage<List<CompanyAnnouncement>> response = new ResponseMessage<>();
        try {
            List<CompanyAnnouncement> list = announcementRepository.findAll();
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Announcements fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch announcements. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishEvent(CompanyAnnouncement announcement) {
        try {
            CultureBroadcastEvent event = new CultureBroadcastEvent(
                    (long) announcement.getAnnouncementId(),
                    announcement.getTitle(),
                    announcement.getTargetDepartment(),
                    announcement.getMessageType().name(),
                    announcement.getAuthorEmail(),
                    announcement.getBroadcastDate()
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.CULTURE_BROADCAST,
                    announcement.getTargetDepartment(),
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish culture broadcast event for announcementId={}", announcement.getAnnouncementId(), e);
        }
    }
}

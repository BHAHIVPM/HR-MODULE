package com.bhahi.hrmodule.service.recruitment;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.recruitment.CandidateNotificationEvent;
import com.bhahi.hrmodule.model.recruitment.CandidateProfile;
import com.bhahi.hrmodule.model.recruitment.JobRequisition;
import com.bhahi.hrmodule.repository.recruitment.CandidateProfileRepository;
import com.bhahi.hrmodule.repository.recruitment.JobRequisitionRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final CandidateProfileRepository candidateRepository;
    private final JobRequisitionRepository jobRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<CandidateProfile> save(CandidateProfile candidate) {
        ResponseMessage<CandidateProfile> response = new ResponseMessage<>();
        try {
            if (candidate.getStage() == null) {
                candidate.setStage(CandidateProfile.CandidateStage.APPLIED);
            }
            if (candidate.getAppliedAt() == null) {
                candidate.setAppliedAt(LocalDate.now());
            }
            CandidateProfile saved = candidateRepository.save(candidate);

            // Publish async notification via Kafka
            publishNotification(saved, "APPLICATION_RECEIVED");

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Candidate profile saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save candidate profile. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<CandidateProfile> findById(int candidateId) {
        ResponseMessage<CandidateProfile> response = new ResponseMessage<>();
        try {
            Optional<CandidateProfile> opt = candidateRepository.findById(candidateId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Candidate profile not found.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(opt.get());
            response.setHeader("Success");
            response.setMessage("Candidate profile fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch candidate profile. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<CandidateProfile>> findByJobId(int jobId) {
        ResponseMessage<List<CandidateProfile>> response = new ResponseMessage<>();
        try {
            List<CandidateProfile> list = candidateRepository.findByJobId(jobId);
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Candidates fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch candidates. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<CandidateProfile> updateStage(int candidateId, CandidateProfile.CandidateStage newStage) {
        ResponseMessage<CandidateProfile> response = new ResponseMessage<>();
        try {
            Optional<CandidateProfile> opt = candidateRepository.findById(candidateId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Candidate profile not found.");
                response.setStatusCode(404);
                return response;
            }
            CandidateProfile existing = opt.get();
            CandidateProfile.CandidateStage oldStage = existing.getStage();
            existing.setStage(newStage);
            CandidateProfile saved = candidateRepository.save(existing);

            // Trigger Kafka notification based on stage change
            String template = switch (newStage) {
                case SHORTLISTED -> "CANDIDATE_SHORTLISTED";
                case INTERVIEW -> "INTERVIEW_INVITE";
                case OFFERED -> "OFFER_LETTER";
                case REJECTED -> "CANDIDATE_REJECTED";
                case HIRED -> "WELCOME_ABOARD";
                default -> "STATUS_UPDATE";
            };
            publishNotification(saved, template);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Candidate stage updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update candidate stage. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishNotification(CandidateProfile candidate, String templateType) {
        try {
            String jobTitle = jobRepository.findById(candidate.getJobId())
                    .map(JobRequisition::getJobTitle)
                    .orElse("Open Position");

            CandidateNotificationEvent event = new CandidateNotificationEvent(
                    (long) candidate.getCandidateId(),
                    candidate.getEmail(),
                    candidate.getFullName(),
                    jobTitle,
                    candidate.getStage().name(),
                    templateType
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.CANDIDATE_NOTIFICATION,
                    candidate.getEmail(),
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish candidate notification event for candidateId={}", candidate.getCandidateId(), e);
        }
    }
}

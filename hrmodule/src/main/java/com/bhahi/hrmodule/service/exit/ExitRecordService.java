package com.bhahi.hrmodule.service.exit;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.exit.ExitClearanceEvent;
import com.bhahi.hrmodule.model.exit.ExitRecord;
import com.bhahi.hrmodule.repository.exit.ExitRecordRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExitRecordService {

    private final ExitRecordRepository exitRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<ExitRecord> submitResignation(ExitRecord record) {
        ResponseMessage<ExitRecord> response = new ResponseMessage<>();
        try {
            if (record.getStatus() == null) {
                record.setStatus(ExitRecord.ExitStatus.SUBMITTED);
            }
            if (record.getResignationDate() == null) {
                record.setResignationDate(LocalDate.now());
            }
            ExitRecord saved = exitRepository.save(record);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Resignation submitted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Submission failed");
            response.setMessage("Could not submit resignation. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<ExitRecord> findByEmployeeId(int employeeId) {
        ResponseMessage<ExitRecord> response = new ResponseMessage<>();
        try {
            Optional<ExitRecord> opt = exitRepository.findByEmployeeId(employeeId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Exit record not found.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(opt.get());
            response.setHeader("Success");
            response.setMessage("Exit record fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch exit record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishEvent(ExitRecord record) {
        try {
            ExitClearanceEvent event = new ExitClearanceEvent(
                    (long) record.getExitId(),
                    record.getEmployeeId(),
                    record.getEmployeeEmail(),
                    record.getResignationDate(),
                    record.getLastWorkingDay(),
                    record.getClearanceDepartment(),
                    record.getStatus().name()
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.EXIT_CLEARANCE,
                    record.getEmployeeEmail() != null ? record.getEmployeeEmail() : "employee",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish exit clearance event for exitId={}", record.getExitId(), e);
        }
    }
}

package com.bhahi.hrmodule.service.benefits;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.benefits.BenefitEnrollmentEvent;
import com.bhahi.hrmodule.model.benefits.EmployeeBenefit;
import com.bhahi.hrmodule.repository.benefits.EmployeeBenefitRepository;
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
public class EmployeeBenefitService {

    private final EmployeeBenefitRepository benefitRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<EmployeeBenefit> enroll(EmployeeBenefit benefit) {
        ResponseMessage<EmployeeBenefit> response = new ResponseMessage<>();
        try {
            if (benefit.getStatus() == null) {
                benefit.setStatus(EmployeeBenefit.BenefitStatus.ACTIVE);
            }
            if (benefit.getEffectiveDate() == null) {
                benefit.setEffectiveDate(LocalDate.now());
            }
            EmployeeBenefit saved = benefitRepository.save(benefit);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Benefit enrollment saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Enrollment failed");
            response.setMessage("Could not save benefit enrollment. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeBenefit>> findByEmployeeId(int employeeId) {
        ResponseMessage<List<EmployeeBenefit>> response = new ResponseMessage<>();
        try {
            List<EmployeeBenefit> list = benefitRepository.findByEmployeeId(employeeId);
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Employee benefits fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch employee benefits. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishEvent(EmployeeBenefit benefit) {
        try {
            BenefitEnrollmentEvent event = new BenefitEnrollmentEvent(
                    (long) benefit.getEnrollmentId(),
                    benefit.getEmployeeId(),
                    benefit.getEmployeeEmail(),
                    benefit.getPlanType().name(),
                    benefit.getPlanName(),
                    benefit.getEffectiveDate(),
                    benefit.getStatus().name()
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.BENEFIT_ENROLLMENT,
                    benefit.getEmployeeEmail() != null ? benefit.getEmployeeEmail() : "employee",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish benefit enrollment event for enrollmentId={}", benefit.getEnrollmentId(), e);
        }
    }
}

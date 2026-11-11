package com.bhahi.hrmodule.service.expense;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.expense.ExpenseApprovalEvent;
import com.bhahi.hrmodule.model.expense.ExpenseClaim;
import com.bhahi.hrmodule.repository.expense.ExpenseClaimRepository;
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
public class ExpenseClaimService {

    private final ExpenseClaimRepository expenseRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<ExpenseClaim> save(ExpenseClaim claim) {
        ResponseMessage<ExpenseClaim> response = new ResponseMessage<>();
        try {
            if (claim.getStatus() == null) {
                claim.setStatus(ExpenseClaim.ClaimStatus.PENDING);
            }
            if (claim.getSubmittedAt() == null) {
                claim.setSubmittedAt(LocalDate.now());
            }
            if (claim.getCurrency() == null) {
                claim.setCurrency("USD");
            }
            ExpenseClaim saved = expenseRepository.save(claim);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Expense claim submitted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not submit expense claim. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<ExpenseClaim> findById(int claimId) {
        ResponseMessage<ExpenseClaim> response = new ResponseMessage<>();
        try {
            Optional<ExpenseClaim> opt = expenseRepository.findById(claimId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Expense claim not found.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(opt.get());
            response.setHeader("Success");
            response.setMessage("Expense claim fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch expense claim. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<ExpenseClaim>> findByEmployeeId(int employeeId) {
        ResponseMessage<List<ExpenseClaim>> response = new ResponseMessage<>();
        try {
            List<ExpenseClaim> list = expenseRepository.findByEmployeeId(employeeId);
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Expense claims fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch expense claims. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<ExpenseClaim> updateStatus(int claimId, ExpenseClaim.ClaimStatus status) {
        ResponseMessage<ExpenseClaim> response = new ResponseMessage<>();
        try {
            Optional<ExpenseClaim> opt = expenseRepository.findById(claimId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Expense claim not found.");
                response.setStatusCode(404);
                return response;
            }
            ExpenseClaim existing = opt.get();
            existing.setStatus(status);
            ExpenseClaim saved = expenseRepository.save(existing);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Expense claim status updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update expense claim status. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishEvent(ExpenseClaim claim) {
        try {
            ExpenseApprovalEvent event = new ExpenseApprovalEvent(
                    (long) claim.getClaimId(),
                    claim.getEmployeeId(),
                    claim.getManagerEmail(),
                    claim.getAmount(),
                    claim.getCurrency(),
                    claim.getStatus().name(),
                    claim.getDescription()
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.EXPENSE_APPROVAL,
                    claim.getManagerEmail() != null ? claim.getManagerEmail() : "finance@company.com",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish expense approval event for claimId={}", claim.getClaimId(), e);
        }
    }
}

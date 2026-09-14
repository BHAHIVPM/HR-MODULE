package com.bhahi.hrmodule.service.bank;

import com.bhahi.hrmodule.model.bank.BankDetails;
import com.bhahi.hrmodule.repository.bank.BankDetailsRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankDetailsService {

    private final BankDetailsRepo bankDetailsRepo;

    // Only one bank record per employee can be primary - unset any existing primary first.
    private void clearExistingPrimary(int employeeId) {
        bankDetailsRepo.findByEmployeeIdAndIsPrimaryTrue(employeeId).ifPresent(existing -> {
            existing.setPrimary(false);
            bankDetailsRepo.save(existing);
        });
    }

    @Transactional
    public ResponseMessage<BankDetails> save(BankDetails bankDetails) {
        ResponseMessage<BankDetails> response = new ResponseMessage<>();
        try {
            if (bankDetails.getStatus() == null) {
                bankDetails.setStatus(BankDetails.BankDetailsStatus.ACTIVE);
            }
            if (bankDetails.isPrimary()) {
                clearExistingPrimary(bankDetails.getEmployeeId());
            }
            BankDetails saved = bankDetailsRepo.save(bankDetails);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Bank details saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save bank details. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<BankDetails> findById(int bankDetailId) {
        ResponseMessage<BankDetails> response = new ResponseMessage<>();
        try {
            Optional<BankDetails> bankDetails = bankDetailsRepo.findById(bankDetailId);
            if (bankDetails.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No bank details found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(bankDetails.get());
            response.setHeader("Success");
            response.setMessage("Bank details fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch bank details. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<BankDetails>> findByEmployee(int employeeId) {
        ResponseMessage<List<BankDetails>> response = new ResponseMessage<>();
        try {
            List<BankDetails> records = bankDetailsRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Bank details fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch bank details. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<BankDetails>> findAll() {
        ResponseMessage<List<BankDetails>> response = new ResponseMessage<>();
        try {
            List<BankDetails> records = bankDetailsRepo.findAll();
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Bank details fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch bank details. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    @Transactional
    public ResponseMessage<BankDetails> update(int bankDetailId, BankDetails updates) {
        ResponseMessage<BankDetails> response = new ResponseMessage<>();
        try {
            Optional<BankDetails> existingOpt = bankDetailsRepo.findById(bankDetailId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No bank details found for this id.");
                response.setStatusCode(404);
                return response;
            }

            BankDetails existing = existingOpt.get();
            existing.setBankName(updates.getBankName());
            existing.setBranchName(updates.getBranchName());
            existing.setAccountNumber(updates.getAccountNumber());
            existing.setIfscCode(updates.getIfscCode());
            existing.setAccountHolderName(updates.getAccountHolderName());
            existing.setAccountType(updates.getAccountType());
            existing.setStatus(updates.getStatus());

            if (updates.isPrimary() && !existing.isPrimary()) {
                clearExistingPrimary(existing.getEmployeeId());
            }
            existing.setPrimary(updates.isPrimary());

            BankDetails saved = bankDetailsRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Bank details updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update bank details. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int bankDetailId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!bankDetailsRepo.existsById(bankDetailId)) {
                response.setHeader("Not found");
                response.setMessage("No bank details found for this id.");
                response.setStatusCode(404);
                return response;
            }
            bankDetailsRepo.deleteById(bankDetailId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Bank details deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete bank details. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

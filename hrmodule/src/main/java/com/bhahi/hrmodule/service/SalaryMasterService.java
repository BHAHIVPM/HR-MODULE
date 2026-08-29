package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.SalaryMaster;
import com.bhahi.hrmodule.repository.SalaryMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryMasterService {

    private final SalaryMasterRepo salaryMasterRepo;

    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    // gross = sum of all earning components. net = gross - sum of all deduction components.
    private void computeTotals(SalaryMaster salary) {
        BigDecimal gross = orZero(salary.getBasicSalary())
                .add(orZero(salary.getHra()))
                .add(orZero(salary.getConveyanceAllowance()))
                .add(orZero(salary.getMedicalAllowance()))
                .add(orZero(salary.getSpecialAllowance()))
                .add(orZero(salary.getOtherAllowance()));

        BigDecimal deductions = orZero(salary.getProvidentFund())
                .add(orZero(salary.getProfessionalTax()))
                .add(orZero(salary.getIncomeTax()))
                .add(orZero(salary.getOtherDeductions()));

        salary.setGrossSalary(gross);
        salary.setNetSalary(gross.subtract(deductions));
    }

    public ResponseMessage<SalaryMaster> save(SalaryMaster salary) {
        ResponseMessage<SalaryMaster> response = new ResponseMessage<>();
        try {
            if (salary.getStatus() == null) {
                salary.setStatus(SalaryMaster.SalaryStatus.ACTIVE);
            }
            computeTotals(salary);
            SalaryMaster saved = salaryMasterRepo.save(salary);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Salary record saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the salary record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<SalaryMaster> findById(int salaryId) {
        ResponseMessage<SalaryMaster> response = new ResponseMessage<>();
        try {
            Optional<SalaryMaster> salary = salaryMasterRepo.findById(salaryId);
            if (salary.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No salary record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(salary.get());
            response.setHeader("Success");
            response.setMessage("Salary record fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the salary record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<SalaryMaster>> findByEmployee(int employeeId) {
        ResponseMessage<List<SalaryMaster>> response = new ResponseMessage<>();
        try {
            List<SalaryMaster> salaries = salaryMasterRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(salaries);
            response.setHeader("Success");
            response.setMessage("Salary history fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch salary history. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<SalaryMaster>> findAll() {
        ResponseMessage<List<SalaryMaster>> response = new ResponseMessage<>();
        try {
            List<SalaryMaster> salaries = salaryMasterRepo.findAll();
            response.setResponseOutput(salaries);
            response.setHeader("Success");
            response.setMessage("Salary records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch salary records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<SalaryMaster> update(int salaryId, SalaryMaster updates) {
        ResponseMessage<SalaryMaster> response = new ResponseMessage<>();
        try {
            Optional<SalaryMaster> existingOpt = salaryMasterRepo.findById(salaryId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No salary record found for this id.");
                response.setStatusCode(404);
                return response;
            }

            SalaryMaster existing = existingOpt.get();
            existing.setBasicSalary(updates.getBasicSalary());
            existing.setHra(updates.getHra());
            existing.setConveyanceAllowance(updates.getConveyanceAllowance());
            existing.setMedicalAllowance(updates.getMedicalAllowance());
            existing.setSpecialAllowance(updates.getSpecialAllowance());
            existing.setOtherAllowance(updates.getOtherAllowance());
            existing.setProvidentFund(updates.getProvidentFund());
            existing.setProfessionalTax(updates.getProfessionalTax());
            existing.setIncomeTax(updates.getIncomeTax());
            existing.setOtherDeductions(updates.getOtherDeductions());
            existing.setEffectiveFrom(updates.getEffectiveFrom());
            existing.setStatus(updates.getStatus());
            computeTotals(existing);

            SalaryMaster saved = salaryMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Salary record updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the salary record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int salaryId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!salaryMasterRepo.existsById(salaryId)) {
                response.setHeader("Not found");
                response.setMessage("No salary record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            salaryMasterRepo.deleteById(salaryId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Salary record deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the salary record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

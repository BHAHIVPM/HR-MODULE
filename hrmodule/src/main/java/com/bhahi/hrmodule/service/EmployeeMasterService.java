package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.EmployeeMaster;
import com.bhahi.hrmodule.repository.EmployeeMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeMasterService {

    private final EmployeeMasterRepo employeeMasterRepo;

    public ResponseMessage<EmployeeMaster> save(EmployeeMaster employee) {
        ResponseMessage<EmployeeMaster> response = new ResponseMessage<>();
        try {
            if (employee.getStatus() == null) {
                employee.setStatus(EmployeeMaster.EmployeeStatus.ACTIVE);
            }
            EmployeeMaster saved = employeeMasterRepo.save(employee);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Employee saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<EmployeeMaster> findById(int employeeId) {
        ResponseMessage<EmployeeMaster> response = new ResponseMessage<>();
        try {
            Optional<EmployeeMaster> employee = employeeMasterRepo.findById(employeeId);
            if (employee.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No employee found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(employee.get());
            response.setHeader("Success");
            response.setMessage("Employee fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeMaster>> findAllActive() {
        ResponseMessage<List<EmployeeMaster>> response = new ResponseMessage<>();
        try {
            List<EmployeeMaster> employees = employeeMasterRepo.findByStatus(EmployeeMaster.EmployeeStatus.ACTIVE);
            response.setResponseOutput(employees);
            response.setHeader("Success");
            response.setMessage("Active employees fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch employees. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeMaster>> findAll() {
        ResponseMessage<List<EmployeeMaster>> response = new ResponseMessage<>();
        try {
            List<EmployeeMaster> employees = employeeMasterRepo.findAll();
            response.setResponseOutput(employees);
            response.setHeader("Success");
            response.setMessage("Employees fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch employees. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // employeeCode intentionally left out of the copy - treated as immutable once assigned.
    public ResponseMessage<EmployeeMaster> update(int employeeId, EmployeeMaster updates) {
        ResponseMessage<EmployeeMaster> response = new ResponseMessage<>();
        try {
            Optional<EmployeeMaster> existingOpt = employeeMasterRepo.findById(employeeId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No employee found for this id.");
                response.setStatusCode(404);
                return response;
            }

            EmployeeMaster existing = existingOpt.get();
            existing.setLoginId(updates.getLoginId());
            existing.setFirstName(updates.getFirstName());
            existing.setLastName(updates.getLastName());
            existing.setEmail(updates.getEmail());
            existing.setMobileNo(updates.getMobileNo());
            existing.setDateOfBirth(updates.getDateOfBirth());
            existing.setDateOfJoining(updates.getDateOfJoining());
            existing.setDepartment(updates.getDepartment());
            existing.setDesignation(updates.getDesignation());
            existing.setReportingManagerId(updates.getReportingManagerId());
            existing.setStatus(updates.getStatus());

            EmployeeMaster saved = employeeMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Employee updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int employeeId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!employeeMasterRepo.existsById(employeeId)) {
                response.setHeader("Not found");
                response.setMessage("No employee found for this id.");
                response.setStatusCode(404);
                return response;
            }
            employeeMasterRepo.deleteById(employeeId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Employee deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

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
}

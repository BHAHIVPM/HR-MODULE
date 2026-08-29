package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.EmployeeShift;
import com.bhahi.hrmodule.repository.EmployeeShiftRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeShiftService {

    private final EmployeeShiftRepo employeeShiftRepo;

    // Closes out whatever shift assignment is currently ACTIVE for this employee, so an
    // employee never has two shifts marked active at the same time.
    private void closeCurrentShift(int employeeId, LocalDate endDate) {
        employeeShiftRepo.findByEmployeeIdAndStatus(employeeId, EmployeeShift.EmployeeShiftStatus.ACTIVE)
                .ifPresent(current -> {
                    current.setStatus(EmployeeShift.EmployeeShiftStatus.INACTIVE);
                    current.setEffectiveTo(endDate);
                    employeeShiftRepo.save(current);
                });
    }

    @Transactional
    public ResponseMessage<EmployeeShift> assign(EmployeeShift employeeShift) {
        ResponseMessage<EmployeeShift> response = new ResponseMessage<>();
        try {
            closeCurrentShift(employeeShift.getEmployeeId(), employeeShift.getEffectiveFrom().minusDays(1));
            employeeShift.setStatus(EmployeeShift.EmployeeShiftStatus.ACTIVE);
            employeeShift.setEffectiveTo(null);
            EmployeeShift saved = employeeShiftRepo.save(employeeShift);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Shift assigned successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not assign the shift. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<EmployeeShift> findById(int employeeShiftId) {
        ResponseMessage<EmployeeShift> response = new ResponseMessage<>();
        try {
            Optional<EmployeeShift> employeeShift = employeeShiftRepo.findById(employeeShiftId);
            if (employeeShift.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No shift assignment found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(employeeShift.get());
            response.setHeader("Success");
            response.setMessage("Shift assignment fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the shift assignment. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<EmployeeShift> findCurrentByEmployee(int employeeId) {
        ResponseMessage<EmployeeShift> response = new ResponseMessage<>();
        try {
            Optional<EmployeeShift> current = employeeShiftRepo.findByEmployeeIdAndStatus(employeeId, EmployeeShift.EmployeeShiftStatus.ACTIVE);
            if (current.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No active shift assignment found for this employee.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(current.get());
            response.setHeader("Success");
            response.setMessage("Current shift fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the current shift. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeShift>> findHistoryByEmployee(int employeeId) {
        ResponseMessage<List<EmployeeShift>> response = new ResponseMessage<>();
        try {
            List<EmployeeShift> history = employeeShiftRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(history);
            response.setHeader("Success");
            response.setMessage("Shift history fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch shift history. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeShift>> findAll() {
        ResponseMessage<List<EmployeeShift>> response = new ResponseMessage<>();
        try {
            List<EmployeeShift> records = employeeShiftRepo.findAll();
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Shift assignments fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch shift assignments. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int employeeShiftId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!employeeShiftRepo.existsById(employeeShiftId)) {
                response.setHeader("Not found");
                response.setMessage("No shift assignment found for this id.");
                response.setStatusCode(404);
                return response;
            }
            employeeShiftRepo.deleteById(employeeShiftId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Shift assignment deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the shift assignment. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

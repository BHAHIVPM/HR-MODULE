package com.bhahi.hrmodule.service.leave;

import com.bhahi.hrmodule.model.leave.LeaveApplication;
import com.bhahi.hrmodule.repository.leave.LeaveApplicationRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepo leaveApplicationRepo;

    public ResponseMessage<LeaveApplication> apply(LeaveApplication application) {
        ResponseMessage<LeaveApplication> response = new ResponseMessage<>();
        try {
            application.setStatus(LeaveApplication.LeaveApplicationStatus.PENDING);
            application.setAppliedOn(LocalDateTime.now());
            application.setApprovedBy(null);
            application.setApprovedOn(null);
            LeaveApplication saved = leaveApplicationRepo.save(application);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Leave application submitted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not submit the leave application. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<LeaveApplication> findById(int leaveApplicationId) {
        ResponseMessage<LeaveApplication> response = new ResponseMessage<>();
        try {
            Optional<LeaveApplication> application = leaveApplicationRepo.findById(leaveApplicationId);
            if (application.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No leave application found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(application.get());
            response.setHeader("Success");
            response.setMessage("Leave application fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the leave application. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<LeaveApplication>> findByEmployee(int employeeId) {
        ResponseMessage<List<LeaveApplication>> response = new ResponseMessage<>();
        try {
            List<LeaveApplication> applications = leaveApplicationRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(applications);
            response.setHeader("Success");
            response.setMessage("Leave applications fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch leave applications. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<LeaveApplication>> findPending() {
        ResponseMessage<List<LeaveApplication>> response = new ResponseMessage<>();
        try {
            List<LeaveApplication> applications = leaveApplicationRepo.findByStatus(LeaveApplication.LeaveApplicationStatus.PENDING);
            response.setResponseOutput(applications);
            response.setHeader("Success");
            response.setMessage("Pending leave applications fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch pending leave applications. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<LeaveApplication>> findAll() {
        ResponseMessage<List<LeaveApplication>> response = new ResponseMessage<>();
        try {
            List<LeaveApplication> applications = leaveApplicationRepo.findAll();
            response.setResponseOutput(applications);
            response.setHeader("Success");
            response.setMessage("Leave applications fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch leave applications. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // approverEmployeeId is the employee_master.employeeId of whoever is actioning the request.
    private ResponseMessage<LeaveApplication> decide(int leaveApplicationId, int approverEmployeeId,
                                                       LeaveApplication.LeaveApplicationStatus decision, String remarks) {
        ResponseMessage<LeaveApplication> response = new ResponseMessage<>();
        try {
            Optional<LeaveApplication> existingOpt = leaveApplicationRepo.findById(leaveApplicationId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No leave application found for this id.");
                response.setStatusCode(404);
                return response;
            }

            LeaveApplication existing = existingOpt.get();
            if (existing.getStatus() != LeaveApplication.LeaveApplicationStatus.PENDING) {
                response.setHeader("Invalid state");
                response.setMessage("Only pending applications can be actioned. Current status: " + existing.getStatus());
                response.setStatusCode(409);
                return response;
            }

            existing.setStatus(decision);
            existing.setApprovedBy(approverEmployeeId);
            existing.setApprovedOn(LocalDateTime.now());
            existing.setRemarks(remarks);

            LeaveApplication saved = leaveApplicationRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Leave application " + decision.name().toLowerCase() + " successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the leave application. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<LeaveApplication> approve(int leaveApplicationId, int approverEmployeeId, String remarks) {
        return decide(leaveApplicationId, approverEmployeeId, LeaveApplication.LeaveApplicationStatus.APPROVED, remarks);
    }

    public ResponseMessage<LeaveApplication> reject(int leaveApplicationId, int approverEmployeeId, String remarks) {
        return decide(leaveApplicationId, approverEmployeeId, LeaveApplication.LeaveApplicationStatus.REJECTED, remarks);
    }

    public ResponseMessage<LeaveApplication> cancel(int leaveApplicationId) {
        ResponseMessage<LeaveApplication> response = new ResponseMessage<>();
        try {
            Optional<LeaveApplication> existingOpt = leaveApplicationRepo.findById(leaveApplicationId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No leave application found for this id.");
                response.setStatusCode(404);
                return response;
            }

            LeaveApplication existing = existingOpt.get();
            existing.setStatus(LeaveApplication.LeaveApplicationStatus.CANCELLED);

            LeaveApplication saved = leaveApplicationRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Leave application cancelled successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not cancel the leave application. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int leaveApplicationId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!leaveApplicationRepo.existsById(leaveApplicationId)) {
                response.setHeader("Not found");
                response.setMessage("No leave application found for this id.");
                response.setStatusCode(404);
                return response;
            }
            leaveApplicationRepo.deleteById(leaveApplicationId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Leave application deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the leave application. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

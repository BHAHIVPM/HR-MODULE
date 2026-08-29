package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.LeaveMaster;
import com.bhahi.hrmodule.repository.LeaveMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveMasterService {

    private final LeaveMasterRepo leaveMasterRepo;

    public ResponseMessage<LeaveMaster> save(LeaveMaster leaveType) {
        ResponseMessage<LeaveMaster> response = new ResponseMessage<>();
        try {
            if (leaveType.getStatus() == null) {
                leaveType.setStatus(LeaveMaster.LeaveMasterStatus.ACTIVE);
            }
            LeaveMaster saved = leaveMasterRepo.save(leaveType);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Leave type saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the leave type. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<LeaveMaster> findById(int leaveTypeId) {
        ResponseMessage<LeaveMaster> response = new ResponseMessage<>();
        try {
            Optional<LeaveMaster> leaveType = leaveMasterRepo.findById(leaveTypeId);
            if (leaveType.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No leave type found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(leaveType.get());
            response.setHeader("Success");
            response.setMessage("Leave type fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the leave type. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<LeaveMaster>> findAllActive() {
        ResponseMessage<List<LeaveMaster>> response = new ResponseMessage<>();
        try {
            List<LeaveMaster> leaveTypes = leaveMasterRepo.findByStatus(LeaveMaster.LeaveMasterStatus.ACTIVE);
            response.setResponseOutput(leaveTypes);
            response.setHeader("Success");
            response.setMessage("Active leave types fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch leave types. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<LeaveMaster>> findAll() {
        ResponseMessage<List<LeaveMaster>> response = new ResponseMessage<>();
        try {
            List<LeaveMaster> leaveTypes = leaveMasterRepo.findAll();
            response.setResponseOutput(leaveTypes);
            response.setHeader("Success");
            response.setMessage("Leave types fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch leave types. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<LeaveMaster> update(int leaveTypeId, LeaveMaster updates) {
        ResponseMessage<LeaveMaster> response = new ResponseMessage<>();
        try {
            Optional<LeaveMaster> existingOpt = leaveMasterRepo.findById(leaveTypeId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No leave type found for this id.");
                response.setStatusCode(404);
                return response;
            }

            LeaveMaster existing = existingOpt.get();
            existing.setLeaveTypeName(updates.getLeaveTypeName());
            existing.setDefaultDaysPerYear(updates.getDefaultDaysPerYear());
            existing.setCarryForwardAllowed(updates.isCarryForwardAllowed());
            existing.setMaxCarryForwardDays(updates.getMaxCarryForwardDays());
            existing.setStatus(updates.getStatus());

            LeaveMaster saved = leaveMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Leave type updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the leave type. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int leaveTypeId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!leaveMasterRepo.existsById(leaveTypeId)) {
                response.setHeader("Not found");
                response.setMessage("No leave type found for this id.");
                response.setStatusCode(404);
                return response;
            }
            leaveMasterRepo.deleteById(leaveTypeId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Leave type deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the leave type. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

package com.bhahi.hrmodule.service.shift;

import com.bhahi.hrmodule.model.shift.ShiftMaster;
import com.bhahi.hrmodule.repository.shift.ShiftMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftMasterService {

    private final ShiftMasterRepo shiftMasterRepo;

    public ResponseMessage<ShiftMaster> save(ShiftMaster shift) {
        ResponseMessage<ShiftMaster> response = new ResponseMessage<>();
        try {
            if (shift.getStatus() == null) {
                shift.setStatus(ShiftMaster.ShiftStatus.ACTIVE);
            }
            ShiftMaster saved = shiftMasterRepo.save(shift);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Shift saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the shift. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<ShiftMaster> findById(int shiftId) {
        ResponseMessage<ShiftMaster> response = new ResponseMessage<>();
        try {
            Optional<ShiftMaster> shift = shiftMasterRepo.findById(shiftId);
            if (shift.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No shift found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(shift.get());
            response.setHeader("Success");
            response.setMessage("Shift fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the shift. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<ShiftMaster>> findAllActive() {
        ResponseMessage<List<ShiftMaster>> response = new ResponseMessage<>();
        try {
            List<ShiftMaster> shifts = shiftMasterRepo.findByStatus(ShiftMaster.ShiftStatus.ACTIVE);
            response.setResponseOutput(shifts);
            response.setHeader("Success");
            response.setMessage("Active shifts fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch shifts. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<ShiftMaster>> findAll() {
        ResponseMessage<List<ShiftMaster>> response = new ResponseMessage<>();
        try {
            List<ShiftMaster> shifts = shiftMasterRepo.findAll();
            response.setResponseOutput(shifts);
            response.setHeader("Success");
            response.setMessage("Shifts fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch shifts. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<ShiftMaster> update(int shiftId, ShiftMaster updates) {
        ResponseMessage<ShiftMaster> response = new ResponseMessage<>();
        try {
            Optional<ShiftMaster> existingOpt = shiftMasterRepo.findById(shiftId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No shift found for this id.");
                response.setStatusCode(404);
                return response;
            }

            ShiftMaster existing = existingOpt.get();
            existing.setShiftName(updates.getShiftName());
            existing.setStartTime(updates.getStartTime());
            existing.setEndTime(updates.getEndTime());
            existing.setBreakDurationMinutes(updates.getBreakDurationMinutes());
            existing.setWeeklyOffDays(updates.getWeeklyOffDays());
            existing.setStatus(updates.getStatus());

            ShiftMaster saved = shiftMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Shift updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the shift. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int shiftId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!shiftMasterRepo.existsById(shiftId)) {
                response.setHeader("Not found");
                response.setMessage("No shift found for this id.");
                response.setStatusCode(404);
                return response;
            }
            shiftMasterRepo.deleteById(shiftId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Shift deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the shift. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.HolidayMaster;
import com.bhahi.hrmodule.repository.HolidayMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HolidayMasterService {

    private final HolidayMasterRepo holidayMasterRepo;

    public ResponseMessage<HolidayMaster> save(HolidayMaster holiday) {
        ResponseMessage<HolidayMaster> response = new ResponseMessage<>();
        try {
            if (holiday.getStatus() == null) {
                holiday.setStatus(HolidayMaster.HolidayStatus.ACTIVE);
            }
            if (holiday.getLocation() == null || holiday.getLocation().isBlank()) {
                holiday.setLocation("ALL");
            }
            HolidayMaster saved = holidayMasterRepo.save(holiday);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Holiday saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the holiday. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<HolidayMaster> findById(int holidayId) {
        ResponseMessage<HolidayMaster> response = new ResponseMessage<>();
        try {
            Optional<HolidayMaster> holiday = holidayMasterRepo.findById(holidayId);
            if (holiday.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No holiday found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(holiday.get());
            response.setHeader("Success");
            response.setMessage("Holiday fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the holiday. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<HolidayMaster>> findAllActive() {
        ResponseMessage<List<HolidayMaster>> response = new ResponseMessage<>();
        try {
            List<HolidayMaster> holidays = holidayMasterRepo.findByStatus(HolidayMaster.HolidayStatus.ACTIVE);
            response.setResponseOutput(holidays);
            response.setHeader("Success");
            response.setMessage("Active holidays fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch holidays. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<HolidayMaster>> findByYear(int year) {
        ResponseMessage<List<HolidayMaster>> response = new ResponseMessage<>();
        try {
            LocalDate from = LocalDate.of(year, 1, 1);
            LocalDate to = LocalDate.of(year, 12, 31);
            List<HolidayMaster> holidays = holidayMasterRepo.findByHolidayDateBetween(from, to);
            response.setResponseOutput(holidays);
            response.setHeader("Success");
            response.setMessage("Holidays for " + year + " fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch holidays. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<HolidayMaster>> findAll() {
        ResponseMessage<List<HolidayMaster>> response = new ResponseMessage<>();
        try {
            List<HolidayMaster> holidays = holidayMasterRepo.findAll();
            response.setResponseOutput(holidays);
            response.setHeader("Success");
            response.setMessage("Holidays fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch holidays. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<HolidayMaster> update(int holidayId, HolidayMaster updates) {
        ResponseMessage<HolidayMaster> response = new ResponseMessage<>();
        try {
            Optional<HolidayMaster> existingOpt = holidayMasterRepo.findById(holidayId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No holiday found for this id.");
                response.setStatusCode(404);
                return response;
            }

            HolidayMaster existing = existingOpt.get();
            existing.setHolidayName(updates.getHolidayName());
            existing.setHolidayDate(updates.getHolidayDate());
            existing.setHolidayType(updates.getHolidayType());
            existing.setLocation(updates.getLocation());
            existing.setDescription(updates.getDescription());
            existing.setStatus(updates.getStatus());

            HolidayMaster saved = holidayMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Holiday updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the holiday. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int holidayId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!holidayMasterRepo.existsById(holidayId)) {
                response.setHeader("Not found");
                response.setMessage("No holiday found for this id.");
                response.setStatusCode(404);
                return response;
            }
            holidayMasterRepo.deleteById(holidayId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Holiday deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the holiday. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

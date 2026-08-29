package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.Attendance;
import com.bhahi.hrmodule.repository.AttendanceRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepo attendanceRepo;

    public ResponseMessage<Attendance> save(Attendance attendance) {
        ResponseMessage<Attendance> response = new ResponseMessage<>();
        try {
            if (attendance.getStatus() == null) {
                attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
            }
            Attendance saved = attendanceRepo.save(attendance);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Attendance record saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the attendance record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // Creates today's row on first call (check-in) and fills checkOutTime + workedHours on a later call.
    public ResponseMessage<Attendance> checkIn(int employeeId) {
        ResponseMessage<Attendance> response = new ResponseMessage<>();
        try {
            LocalDate today = LocalDate.now();
            Optional<Attendance> existing = attendanceRepo.findByEmployeeIdAndAttendanceDate(employeeId, today);
            if (existing.isPresent()) {
                response.setHeader("Already checked in");
                response.setMessage("Attendance already recorded for today.");
                response.setStatusCode(409);
                response.setResponseOutput(existing.get());
                return response;
            }

            Attendance attendance = new Attendance();
            attendance.setEmployeeId(employeeId);
            attendance.setAttendanceDate(today);
            attendance.setCheckInTime(LocalTime.now());
            attendance.setStatus(Attendance.AttendanceStatus.PRESENT);

            Attendance saved = attendanceRepo.save(attendance);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Checked in successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Check-in failed");
            response.setMessage("Could not record check-in. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<Attendance> checkOut(int employeeId) {
        ResponseMessage<Attendance> response = new ResponseMessage<>();
        try {
            LocalDate today = LocalDate.now();
            Optional<Attendance> existingOpt = attendanceRepo.findByEmployeeIdAndAttendanceDate(employeeId, today);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not checked in");
                response.setMessage("No check-in found for today.");
                response.setStatusCode(404);
                return response;
            }

            Attendance existing = existingOpt.get();
            LocalTime checkOutTime = LocalTime.now();
            existing.setCheckOutTime(checkOutTime);
            if (existing.getCheckInTime() != null) {
                double hours = Duration.between(existing.getCheckInTime(), checkOutTime).toMinutes() / 60.0;
                existing.setWorkedHours(Math.round(hours * 100.0) / 100.0);
                existing.setStatus(hours < 4.5 ? Attendance.AttendanceStatus.HALF_DAY : Attendance.AttendanceStatus.PRESENT);
            }

            Attendance saved = attendanceRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Checked out successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Check-out failed");
            response.setMessage("Could not record check-out. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<Attendance> findById(int attendanceId) {
        ResponseMessage<Attendance> response = new ResponseMessage<>();
        try {
            Optional<Attendance> attendance = attendanceRepo.findById(attendanceId);
            if (attendance.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No attendance record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(attendance.get());
            response.setHeader("Success");
            response.setMessage("Attendance record fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the attendance record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Attendance>> findByEmployee(int employeeId) {
        ResponseMessage<List<Attendance>> response = new ResponseMessage<>();
        try {
            List<Attendance> records = attendanceRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Attendance records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch attendance records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Attendance>> findByEmployeeAndRange(int employeeId, LocalDate from, LocalDate to) {
        ResponseMessage<List<Attendance>> response = new ResponseMessage<>();
        try {
            List<Attendance> records = attendanceRepo.findByEmployeeIdAndAttendanceDateBetween(employeeId, from, to);
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Attendance records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch attendance records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Attendance>> findAll() {
        ResponseMessage<List<Attendance>> response = new ResponseMessage<>();
        try {
            List<Attendance> records = attendanceRepo.findAll();
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Attendance records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch attendance records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<Attendance> update(int attendanceId, Attendance updates) {
        ResponseMessage<Attendance> response = new ResponseMessage<>();
        try {
            Optional<Attendance> existingOpt = attendanceRepo.findById(attendanceId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No attendance record found for this id.");
                response.setStatusCode(404);
                return response;
            }

            Attendance existing = existingOpt.get();
            existing.setCheckInTime(updates.getCheckInTime());
            existing.setCheckOutTime(updates.getCheckOutTime());
            existing.setWorkedHours(updates.getWorkedHours());
            existing.setStatus(updates.getStatus());
            existing.setRemarks(updates.getRemarks());

            Attendance saved = attendanceRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Attendance record updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the attendance record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int attendanceId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!attendanceRepo.existsById(attendanceId)) {
                response.setHeader("Not found");
                response.setMessage("No attendance record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            attendanceRepo.deleteById(attendanceId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Attendance record deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the attendance record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

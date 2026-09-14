package com.bhahi.hrmodule.controller.attendance;

import com.bhahi.hrmodule.model.attendance.Attendance;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<Attendance>> save(@RequestBody Attendance attendance) {
        ResponseMessage<Attendance> response = attendanceService.save(attendance);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<ResponseMessage<Attendance>> checkIn(@PathVariable int employeeId) {
        ResponseMessage<Attendance> response = attendanceService.checkIn(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/check-out/{employeeId}")
    public ResponseEntity<ResponseMessage<Attendance>> checkOut(@PathVariable int employeeId) {
        ResponseMessage<Attendance> response = attendanceService.checkOut(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<ResponseMessage<Attendance>> findById(@PathVariable int attendanceId) {
        ResponseMessage<Attendance> response = attendanceService.findById(attendanceId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<Attendance>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<Attendance>> response = attendanceService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}/range")
    public ResponseEntity<ResponseMessage<List<Attendance>>> findByEmployeeAndRange(
            @PathVariable int employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        ResponseMessage<List<Attendance>> response = attendanceService.findByEmployeeAndRange(employeeId, from, to);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<Attendance>>> findAll() {
        ResponseMessage<List<Attendance>> response = attendanceService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{attendanceId}")
    public ResponseEntity<ResponseMessage<Attendance>> update(@PathVariable int attendanceId,
                                                               @RequestBody Attendance updates) {
        ResponseMessage<Attendance> response = attendanceService.update(attendanceId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int attendanceId) {
        ResponseMessage<String> response = attendanceService.delete(attendanceId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

package com.bhahi.hrmodule.controller.employee;

import com.bhahi.hrmodule.model.employee.EmployeeShift;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.employee.EmployeeShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee-shift")
public class EmployeeShiftController {

    private final EmployeeShiftService employeeShiftService;

    @PostMapping("/assign")
    public ResponseEntity<ResponseMessage<EmployeeShift>> assign(@RequestBody EmployeeShift employeeShift) {
        ResponseMessage<EmployeeShift> response = employeeShiftService.assign(employeeShift);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{employeeShiftId}")
    public ResponseEntity<ResponseMessage<EmployeeShift>> findById(@PathVariable int employeeShiftId) {
        ResponseMessage<EmployeeShift> response = employeeShiftService.findById(employeeShiftId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/current/{employeeId}")
    public ResponseEntity<ResponseMessage<EmployeeShift>> findCurrentByEmployee(@PathVariable int employeeId) {
        ResponseMessage<EmployeeShift> response = employeeShiftService.findCurrentByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/history/{employeeId}")
    public ResponseEntity<ResponseMessage<List<EmployeeShift>>> findHistoryByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<EmployeeShift>> response = employeeShiftService.findHistoryByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<EmployeeShift>>> findAll() {
        ResponseMessage<List<EmployeeShift>> response = employeeShiftService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{employeeShiftId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int employeeShiftId) {
        ResponseMessage<String> response = employeeShiftService.delete(employeeShiftId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

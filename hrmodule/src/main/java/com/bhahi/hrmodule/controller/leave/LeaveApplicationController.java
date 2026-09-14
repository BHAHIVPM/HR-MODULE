package com.bhahi.hrmodule.controller.leave;

import com.bhahi.hrmodule.model.leave.LeaveApplication;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.leave.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/leave-application")
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<ResponseMessage<LeaveApplication>> apply(@RequestBody LeaveApplication application) {
        ResponseMessage<LeaveApplication> response = leaveApplicationService.apply(application);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{leaveApplicationId}")
    public ResponseEntity<ResponseMessage<LeaveApplication>> findById(@PathVariable int leaveApplicationId) {
        ResponseMessage<LeaveApplication> response = leaveApplicationService.findById(leaveApplicationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<LeaveApplication>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<LeaveApplication>> response = leaveApplicationService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseMessage<List<LeaveApplication>>> findPending() {
        ResponseMessage<List<LeaveApplication>> response = leaveApplicationService.findPending();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<LeaveApplication>>> findAll() {
        ResponseMessage<List<LeaveApplication>> response = leaveApplicationService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{leaveApplicationId}/approve/{approverEmployeeId}")
    public ResponseEntity<ResponseMessage<LeaveApplication>> approve(@PathVariable int leaveApplicationId,
                                                                      @PathVariable int approverEmployeeId,
                                                                      @RequestParam(required = false) String remarks) {
        ResponseMessage<LeaveApplication> response = leaveApplicationService.approve(leaveApplicationId, approverEmployeeId, remarks);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{leaveApplicationId}/reject/{approverEmployeeId}")
    public ResponseEntity<ResponseMessage<LeaveApplication>> reject(@PathVariable int leaveApplicationId,
                                                                     @PathVariable int approverEmployeeId,
                                                                     @RequestParam(required = false) String remarks) {
        ResponseMessage<LeaveApplication> response = leaveApplicationService.reject(leaveApplicationId, approverEmployeeId, remarks);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{leaveApplicationId}/cancel")
    public ResponseEntity<ResponseMessage<LeaveApplication>> cancel(@PathVariable int leaveApplicationId) {
        ResponseMessage<LeaveApplication> response = leaveApplicationService.cancel(leaveApplicationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{leaveApplicationId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int leaveApplicationId) {
        ResponseMessage<String> response = leaveApplicationService.delete(leaveApplicationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

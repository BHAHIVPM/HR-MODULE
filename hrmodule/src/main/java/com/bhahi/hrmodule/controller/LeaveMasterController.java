package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.model.LeaveMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.LeaveMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/leave-type")
public class LeaveMasterController {

    private final LeaveMasterService leaveMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<LeaveMaster>> save(@RequestBody LeaveMaster leaveType) {
        ResponseMessage<LeaveMaster> response = leaveMasterService.save(leaveType);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{leaveTypeId}")
    public ResponseEntity<ResponseMessage<LeaveMaster>> findById(@PathVariable int leaveTypeId) {
        ResponseMessage<LeaveMaster> response = leaveMasterService.findById(leaveTypeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseMessage<List<LeaveMaster>>> findAllActive() {
        ResponseMessage<List<LeaveMaster>> response = leaveMasterService.findAllActive();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<LeaveMaster>>> findAll() {
        ResponseMessage<List<LeaveMaster>> response = leaveMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{leaveTypeId}")
    public ResponseEntity<ResponseMessage<LeaveMaster>> update(@PathVariable int leaveTypeId,
                                                                @RequestBody LeaveMaster updates) {
        ResponseMessage<LeaveMaster> response = leaveMasterService.update(leaveTypeId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{leaveTypeId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int leaveTypeId) {
        ResponseMessage<String> response = leaveMasterService.delete(leaveTypeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

package com.bhahi.hrmodule.controller.exit;

import com.bhahi.hrmodule.model.exit.ExitRecord;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.exit.ExitRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exit")
public class ExitRecordController {

    private final ExitRecordService exitService;

    @PostMapping("/resignation")
    public ResponseEntity<ResponseMessage<ExitRecord>> submitResignation(@RequestBody ExitRecord record) {
        ResponseMessage<ExitRecord> response = exitService.submitResignation(record);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ResponseMessage<ExitRecord>> findByEmployeeId(@PathVariable int employeeId) {
        ResponseMessage<ExitRecord> response = exitService.findByEmployeeId(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

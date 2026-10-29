package com.bhahi.hrmodule.controller.recruitment;

import com.bhahi.hrmodule.model.recruitment.JobRequisition;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.recruitment.JobRequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment/jobs")
public class JobRequisitionController {

    private final JobRequisitionService jobService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<JobRequisition>> save(@RequestBody JobRequisition requisition) {
        ResponseMessage<JobRequisition> response = jobService.save(requisition);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ResponseMessage<JobRequisition>> findById(@PathVariable int jobId) {
        ResponseMessage<JobRequisition> response = jobService.findById(jobId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<JobRequisition>>> findAll() {
        ResponseMessage<List<JobRequisition>> response = jobService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseMessage<List<JobRequisition>>> findByStatus(@PathVariable JobRequisition.RequisitionStatus status) {
        ResponseMessage<List<JobRequisition>> response = jobService.findByStatus(status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{jobId}")
    public ResponseEntity<ResponseMessage<JobRequisition>> update(@PathVariable int jobId, @RequestBody JobRequisition updates) {
        ResponseMessage<JobRequisition> response = jobService.update(jobId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int jobId) {
        ResponseMessage<String> response = jobService.delete(jobId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

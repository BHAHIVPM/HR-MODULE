package com.bhahi.hrmodule.controller.recruitment;

import com.bhahi.hrmodule.model.recruitment.CandidateProfile;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.recruitment.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment/candidates")
public class CandidateController {

    private final CandidateProfileService candidateService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<CandidateProfile>> save(@RequestBody CandidateProfile candidate) {
        ResponseMessage<CandidateProfile> response = candidateService.save(candidate);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{candidateId}")
    public ResponseEntity<ResponseMessage<CandidateProfile>> findById(@PathVariable int candidateId) {
        ResponseMessage<CandidateProfile> response = candidateService.findById(candidateId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ResponseMessage<List<CandidateProfile>>> findByJobId(@PathVariable int jobId) {
        ResponseMessage<List<CandidateProfile>> response = candidateService.findByJobId(jobId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{candidateId}/stage")
    public ResponseEntity<ResponseMessage<CandidateProfile>> updateStage(
            @PathVariable int candidateId,
            @RequestParam CandidateProfile.CandidateStage stage) {
        ResponseMessage<CandidateProfile> response = candidateService.updateStage(candidateId, stage);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

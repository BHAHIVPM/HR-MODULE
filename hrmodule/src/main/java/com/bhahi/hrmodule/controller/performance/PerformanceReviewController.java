package com.bhahi.hrmodule.controller.performance;

import com.bhahi.hrmodule.model.performance.PerformanceReview;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.performance.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performance-review")
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<PerformanceReview>> save(@RequestBody PerformanceReview review) {
        ResponseMessage<PerformanceReview> response = performanceReviewService.save(review);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseMessage<PerformanceReview>> findById(@PathVariable int reviewId) {
        ResponseMessage<PerformanceReview> response = performanceReviewService.findById(reviewId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<PerformanceReview>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<PerformanceReview>> response = performanceReviewService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-reviewer/{reviewerId}")
    public ResponseEntity<ResponseMessage<List<PerformanceReview>>> findByReviewer(@PathVariable int reviewerId) {
        ResponseMessage<List<PerformanceReview>> response = performanceReviewService.findByReviewer(reviewerId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<PerformanceReview>>> findAll() {
        ResponseMessage<List<PerformanceReview>> response = performanceReviewService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{reviewId}/submit")
    public ResponseEntity<ResponseMessage<PerformanceReview>> submit(@PathVariable int reviewId) {
        ResponseMessage<PerformanceReview> response = performanceReviewService.submit(reviewId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{reviewId}/complete")
    public ResponseEntity<ResponseMessage<PerformanceReview>> complete(@PathVariable int reviewId,
                                                                        @RequestParam Double overallRating,
                                                                        @RequestParam(required = false) String reviewerComments) {
        ResponseMessage<PerformanceReview> response = performanceReviewService.completeReview(reviewId, overallRating, reviewerComments);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{reviewId}/acknowledge")
    public ResponseEntity<ResponseMessage<PerformanceReview>> acknowledge(@PathVariable int reviewId) {
        ResponseMessage<PerformanceReview> response = performanceReviewService.acknowledge(reviewId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ResponseMessage<PerformanceReview>> update(@PathVariable int reviewId,
                                                                      @RequestBody PerformanceReview updates) {
        ResponseMessage<PerformanceReview> response = performanceReviewService.update(reviewId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int reviewId) {
        ResponseMessage<String> response = performanceReviewService.delete(reviewId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.PerformanceReview;
import com.bhahi.hrmodule.repository.PerformanceReviewRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepo performanceReviewRepo;

    public ResponseMessage<PerformanceReview> save(PerformanceReview review) {
        ResponseMessage<PerformanceReview> response = new ResponseMessage<>();
        try {
            if (review.getStatus() == null) {
                review.setStatus(PerformanceReview.ReviewStatus.DRAFT);
            }
            PerformanceReview saved = performanceReviewRepo.save(review);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Performance review saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the performance review. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<PerformanceReview> findById(int reviewId) {
        ResponseMessage<PerformanceReview> response = new ResponseMessage<>();
        try {
            Optional<PerformanceReview> review = performanceReviewRepo.findById(reviewId);
            if (review.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No performance review found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(review.get());
            response.setHeader("Success");
            response.setMessage("Performance review fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the performance review. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<PerformanceReview>> findByEmployee(int employeeId) {
        ResponseMessage<List<PerformanceReview>> response = new ResponseMessage<>();
        try {
            List<PerformanceReview> reviews = performanceReviewRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(reviews);
            response.setHeader("Success");
            response.setMessage("Performance reviews fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch performance reviews. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<PerformanceReview>> findByReviewer(int reviewerId) {
        ResponseMessage<List<PerformanceReview>> response = new ResponseMessage<>();
        try {
            List<PerformanceReview> reviews = performanceReviewRepo.findByReviewerId(reviewerId);
            response.setResponseOutput(reviews);
            response.setHeader("Success");
            response.setMessage("Performance reviews fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch performance reviews. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<PerformanceReview>> findAll() {
        ResponseMessage<List<PerformanceReview>> response = new ResponseMessage<>();
        try {
            List<PerformanceReview> reviews = performanceReviewRepo.findAll();
            response.setResponseOutput(reviews);
            response.setHeader("Success");
            response.setMessage("Performance reviews fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch performance reviews. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<PerformanceReview> submit(int reviewId) {
        return transition(reviewId, PerformanceReview.ReviewStatus.SUBMITTED, null);
    }

    public ResponseMessage<PerformanceReview> completeReview(int reviewId, Double overallRating, String reviewerComments) {
        ResponseMessage<PerformanceReview> response = transition(reviewId, PerformanceReview.ReviewStatus.REVIEWED, LocalDate.now());
        if (response.getStatusCode() == 200) {
            PerformanceReview saved = response.getResponseOutput();
            saved.setOverallRating(overallRating);
            saved.setReviewerComments(reviewerComments);
            performanceReviewRepo.save(saved);
        }
        return response;
    }

    public ResponseMessage<PerformanceReview> acknowledge(int reviewId) {
        return transition(reviewId, PerformanceReview.ReviewStatus.ACKNOWLEDGED, null);
    }

    private ResponseMessage<PerformanceReview> transition(int reviewId, PerformanceReview.ReviewStatus newStatus, LocalDate reviewDate) {
        ResponseMessage<PerformanceReview> response = new ResponseMessage<>();
        try {
            Optional<PerformanceReview> existingOpt = performanceReviewRepo.findById(reviewId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No performance review found for this id.");
                response.setStatusCode(404);
                return response;
            }
            PerformanceReview existing = existingOpt.get();
            existing.setStatus(newStatus);
            if (reviewDate != null) {
                existing.setReviewDate(reviewDate);
            }
            PerformanceReview saved = performanceReviewRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Performance review moved to " + newStatus + ".");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the performance review. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<PerformanceReview> update(int reviewId, PerformanceReview updates) {
        ResponseMessage<PerformanceReview> response = new ResponseMessage<>();
        try {
            Optional<PerformanceReview> existingOpt = performanceReviewRepo.findById(reviewId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No performance review found for this id.");
                response.setStatusCode(404);
                return response;
            }

            PerformanceReview existing = existingOpt.get();
            existing.setReviewerId(updates.getReviewerId());
            existing.setReviewCycle(updates.getReviewCycle());
            existing.setReviewPeriodStart(updates.getReviewPeriodStart());
            existing.setReviewPeriodEnd(updates.getReviewPeriodEnd());
            existing.setOverallRating(updates.getOverallRating());
            existing.setAchievements(updates.getAchievements());
            existing.setStrengths(updates.getStrengths());
            existing.setAreasOfImprovement(updates.getAreasOfImprovement());
            existing.setGoalsForNextCycle(updates.getGoalsForNextCycle());
            existing.setReviewerComments(updates.getReviewerComments());
            existing.setStatus(updates.getStatus());
            existing.setReviewDate(updates.getReviewDate());

            PerformanceReview saved = performanceReviewRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Performance review updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the performance review. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int reviewId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!performanceReviewRepo.existsById(reviewId)) {
                response.setHeader("Not found");
                response.setMessage("No performance review found for this id.");
                response.setStatusCode(404);
                return response;
            }
            performanceReviewRepo.deleteById(reviewId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Performance review deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the performance review. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

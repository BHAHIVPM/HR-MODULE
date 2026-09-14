package com.bhahi.hrmodule.repository.performance;

import com.bhahi.hrmodule.model.performance.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepo extends JpaRepository<PerformanceReview, Integer> {

    List<PerformanceReview> findByEmployeeId(Integer employeeId);

    List<PerformanceReview> findByReviewerId(Integer reviewerId);

    List<PerformanceReview> findByEmployeeIdAndReviewCycle(Integer employeeId, String reviewCycle);

    List<PerformanceReview> findByStatus(PerformanceReview.ReviewStatus status);
}

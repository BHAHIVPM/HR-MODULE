package com.bhahi.hrmodule.repository.training;

import com.bhahi.hrmodule.model.training.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Integer> {
    List<CourseEnrollment> findByEmployeeId(int employeeId);
    List<CourseEnrollment> findByCourseId(int courseId);
}

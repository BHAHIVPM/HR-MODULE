package com.bhahi.hrmodule.repository.training;

import com.bhahi.hrmodule.model.training.TrainingCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingCourseRepository extends JpaRepository<TrainingCourse, Integer> {
}

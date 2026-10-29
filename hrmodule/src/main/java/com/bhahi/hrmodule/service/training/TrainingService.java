package com.bhahi.hrmodule.service.training;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.training.TrainingAssignmentEvent;
import com.bhahi.hrmodule.model.training.CourseEnrollment;
import com.bhahi.hrmodule.model.training.TrainingCourse;
import com.bhahi.hrmodule.repository.training.CourseEnrollmentRepository;
import com.bhahi.hrmodule.repository.training.TrainingCourseRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingCourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<TrainingCourse> saveCourse(TrainingCourse course) {
        ResponseMessage<TrainingCourse> response = new ResponseMessage<>();
        try {
            TrainingCourse saved = courseRepository.save(course);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Training course saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save training course. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<TrainingCourse>> findAllCourses() {
        ResponseMessage<List<TrainingCourse>> response = new ResponseMessage<>();
        try {
            List<TrainingCourse> list = courseRepository.findAll();
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Training courses fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch training courses. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<CourseEnrollment> assignCourse(CourseEnrollment enrollment) {
        ResponseMessage<CourseEnrollment> response = new ResponseMessage<>();
        try {
            if (enrollment.getStatus() == null) {
                enrollment.setStatus(CourseEnrollment.EnrollmentStatus.ASSIGNED);
            }
            if (enrollment.getEnrolledAt() == null) {
                enrollment.setEnrolledAt(LocalDate.now());
            }
            CourseEnrollment saved = enrollmentRepository.save(enrollment);

            publishEvent(saved, "COURSE_ASSIGNMENT");

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Course assigned successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Assignment failed");
            response.setMessage("Could not assign course. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<CourseEnrollment>> findEnrollmentsByEmployee(int employeeId) {
        ResponseMessage<List<CourseEnrollment>> response = new ResponseMessage<>();
        try {
            List<CourseEnrollment> list = enrollmentRepository.findByEmployeeId(employeeId);
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Course enrollments fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch course enrollments. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishEvent(CourseEnrollment enrollment, String reminderType) {
        try {
            String courseName = courseRepository.findById(enrollment.getCourseId())
                    .map(TrainingCourse::getCourseName)
                    .orElse("General Training");

            TrainingAssignmentEvent event = new TrainingAssignmentEvent(
                    (long) enrollment.getEnrollmentId(),
                    enrollment.getEmployeeId(),
                    enrollment.getEmployeeEmail(),
                    enrollment.getCourseId(),
                    courseName,
                    enrollment.getDueDate(),
                    reminderType
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.TRAINING_ASSIGNMENT,
                    enrollment.getEmployeeEmail() != null ? enrollment.getEmployeeEmail() : "employee",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish training assignment event for enrollmentId={}", enrollment.getEnrollmentId(), e);
        }
    }
}

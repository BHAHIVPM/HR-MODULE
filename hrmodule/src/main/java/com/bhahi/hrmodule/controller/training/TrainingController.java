package com.bhahi.hrmodule.controller.training;

import com.bhahi.hrmodule.model.training.CourseEnrollment;
import com.bhahi.hrmodule.model.training.TrainingCourse;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.training.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lms")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping("/courses/save")
    public ResponseEntity<ResponseMessage<TrainingCourse>> saveCourse(@RequestBody TrainingCourse course) {
        ResponseMessage<TrainingCourse> response = trainingService.saveCourse(course);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/courses/all")
    public ResponseEntity<ResponseMessage<List<TrainingCourse>>> findAllCourses() {
        ResponseMessage<List<TrainingCourse>> response = trainingService.findAllCourses();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/enrollments/assign")
    public ResponseEntity<ResponseMessage<CourseEnrollment>> assignCourse(@RequestBody CourseEnrollment enrollment) {
        ResponseMessage<CourseEnrollment> response = trainingService.assignCourse(enrollment);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/enrollments/employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<CourseEnrollment>>> findEnrollmentsByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<CourseEnrollment>> response = trainingService.findEnrollmentsByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

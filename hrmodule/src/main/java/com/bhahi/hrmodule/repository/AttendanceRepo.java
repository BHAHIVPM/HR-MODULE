package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByEmployeeId(Integer employeeId);

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Integer employeeId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDateBetween(LocalDate from, LocalDate to);

    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Integer employeeId, LocalDate from, LocalDate to);
}

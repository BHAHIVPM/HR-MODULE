package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.EmployeeShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeShiftRepo extends JpaRepository<EmployeeShift, Integer> {

    List<EmployeeShift> findByEmployeeId(Integer employeeId);

    // The row with no end date (or the most recent one) is the employee's current shift.
    Optional<EmployeeShift> findByEmployeeIdAndStatus(Integer employeeId, EmployeeShift.EmployeeShiftStatus status);

    List<EmployeeShift> findByShiftId(Integer shiftId);
}

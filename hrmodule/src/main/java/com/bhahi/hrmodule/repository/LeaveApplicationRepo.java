package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepo extends JpaRepository<LeaveApplication, Integer> {

    List<LeaveApplication> findByEmployeeId(Integer employeeId);

    List<LeaveApplication> findByStatus(LeaveApplication.LeaveApplicationStatus status);

    List<LeaveApplication> findByEmployeeIdAndStatus(Integer employeeId, LeaveApplication.LeaveApplicationStatus status);
}

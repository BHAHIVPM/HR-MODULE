package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.EmployeeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeMasterRepo extends JpaRepository<EmployeeMaster, Integer> {

    Optional<EmployeeMaster> findByEmployeeCode(String employeeCode);

    List<EmployeeMaster> findByStatus(EmployeeMaster.EmployeeStatus status);

    List<EmployeeMaster> findByReportingManagerId(Integer reportingManagerId);
}

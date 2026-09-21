package com.bhahi.hrmodule.repository.employee;

import com.bhahi.hrmodule.model.employee.EmployeeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeMasterRepo extends JpaRepository<EmployeeMaster, Integer> {

    Optional<EmployeeMaster> findByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
    Optional<EmployeeMaster> findByEmail(String email);
    Optional<EmployeeMaster> findByMobileNo(String mobileNo);

    List<EmployeeMaster> findByStatus(EmployeeMaster.EmployeeStatus status);

    List<EmployeeMaster> findByReportingManagerId(Integer reportingManagerId);
}

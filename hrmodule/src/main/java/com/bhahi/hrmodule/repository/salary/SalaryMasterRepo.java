package com.bhahi.hrmodule.repository.salary;

import com.bhahi.hrmodule.model.salary.SalaryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryMasterRepo extends JpaRepository<SalaryMaster, Integer> {

    List<SalaryMaster> findByEmployeeId(Integer employeeId);

    // The single currently-active salary record for an employee, if any.
    Optional<SalaryMaster> findByEmployeeIdAndStatus(Integer employeeId, SalaryMaster.SalaryStatus status);

    List<SalaryMaster> findByStatus(SalaryMaster.SalaryStatus status);
}

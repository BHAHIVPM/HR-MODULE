package com.bhahi.hrmodule.repository.benefits;

import com.bhahi.hrmodule.model.benefits.EmployeeBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeBenefitRepository extends JpaRepository<EmployeeBenefit, Integer> {
    List<EmployeeBenefit> findByEmployeeId(int employeeId);
}

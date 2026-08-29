package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankDetailsRepo extends JpaRepository<BankDetails, Integer> {

    List<BankDetails> findByEmployeeId(Integer employeeId);

    Optional<BankDetails> findByEmployeeIdAndIsPrimaryTrue(Integer employeeId);
}

package com.bhahi.hrmodule.repository.expense;

import com.bhahi.hrmodule.model.expense.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, Integer> {
    List<ExpenseClaim> findByEmployeeId(int employeeId);
    List<ExpenseClaim> findByStatus(ExpenseClaim.ClaimStatus status);
}

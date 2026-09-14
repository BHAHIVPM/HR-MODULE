package com.bhahi.hrmodule.repository.payroll;

import com.bhahi.hrmodule.model.payroll.PayrollTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollTransactionRepo extends JpaRepository<PayrollTransaction, Integer> {

    List<PayrollTransaction> findByEmployeeId(Integer employeeId);

    Optional<PayrollTransaction> findByEmployeeIdAndPayrollMonthAndPayrollYear(Integer employeeId, Integer payrollMonth, Integer payrollYear);

    List<PayrollTransaction> findByPayrollMonthAndPayrollYear(Integer payrollMonth, Integer payrollYear);

    List<PayrollTransaction> findByStatus(PayrollTransaction.PayrollStatus status);
}

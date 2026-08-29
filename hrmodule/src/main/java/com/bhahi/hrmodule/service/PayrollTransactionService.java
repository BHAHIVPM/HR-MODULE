package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.Attendance;
import com.bhahi.hrmodule.model.LeaveApplication;
import com.bhahi.hrmodule.model.PayrollTransaction;
import com.bhahi.hrmodule.model.SalaryMaster;
import com.bhahi.hrmodule.repository.AttendanceRepo;
import com.bhahi.hrmodule.repository.LeaveApplicationRepo;
import com.bhahi.hrmodule.repository.PayrollTransactionRepo;
import com.bhahi.hrmodule.repository.SalaryMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

// Generates one payslip per employee per month by pulling from three other modules:
//  - salary_master  -> the pay structure (gross/net) to bill against
//  - attendance     -> how many days were actually PRESENT/HALF_DAY that month
//  - leave_application -> how many APPROVED leave days overlap that month (treated as paid)
// Any working day not covered by presence or approved leave is Loss-Of-Pay (LOP) and is
// deducted pro-rata from the gross salary.
@Service
@RequiredArgsConstructor
public class PayrollTransactionService {

    private final PayrollTransactionRepo payrollTransactionRepo;
    private final SalaryMasterRepo salaryMasterRepo;
    private final AttendanceRepo attendanceRepo;
    private final LeaveApplicationRepo leaveApplicationRepo;

    @Transactional
    public ResponseMessage<PayrollTransaction> generate(int employeeId, int month, int year) {
        ResponseMessage<PayrollTransaction> response = new ResponseMessage<>();
        try {
            Optional<SalaryMaster> salaryOpt = salaryMasterRepo.findByEmployeeIdAndStatus(employeeId, SalaryMaster.SalaryStatus.ACTIVE);
            if (salaryOpt.isEmpty()) {
                response.setHeader("No active salary");
                response.setMessage("No active salary structure found for this employee. Add one via /salary/save first.");
                response.setStatusCode(404);
                return response;
            }
            SalaryMaster salary = salaryOpt.get();

            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            int workingDays = yearMonth.lengthOfMonth();

            List<Attendance> attendanceRecords = attendanceRepo.findByEmployeeIdAndAttendanceDateBetween(employeeId, monthStart, monthEnd);
            long presentDays = attendanceRecords.stream()
                    .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT || a.getStatus() == Attendance.AttendanceStatus.HALF_DAY)
                    .count();

            List<LeaveApplication> leaveRecords = leaveApplicationRepo.findByEmployeeIdAndStatus(employeeId, LeaveApplication.LeaveApplicationStatus.APPROVED);
            double paidLeaveDays = leaveRecords.stream()
                    .filter(l -> !l.getToDate().isBefore(monthStart) && !l.getFromDate().isAfter(monthEnd))
                    .mapToDouble(LeaveApplication::getNoOfDays)
                    .sum();

            double lopDays = Math.max(0.0, workingDays - presentDays - paidLeaveDays);

            BigDecimal gross = salary.getGrossSalary() == null ? BigDecimal.ZERO : salary.getGrossSalary();
            BigDecimal structuralDeductions = gross.subtract(salary.getNetSalary() == null ? BigDecimal.ZERO : salary.getNetSalary());

            BigDecimal perDayRate = workingDays == 0 ? BigDecimal.ZERO
                    : gross.divide(BigDecimal.valueOf(workingDays), 4, RoundingMode.HALF_UP);
            BigDecimal lopDeduction = perDayRate.multiply(BigDecimal.valueOf(lopDays)).setScale(2, RoundingMode.HALF_UP);

            BigDecimal totalDeductions = structuralDeductions.add(lopDeduction).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netPay = gross.subtract(totalDeductions).setScale(2, RoundingMode.HALF_UP);

            // Regenerate in place if a payslip for this employee/month/year already exists,
            // rather than creating a duplicate (unique constraint would reject it anyway).
            PayrollTransaction payroll = payrollTransactionRepo
                    .findByEmployeeIdAndPayrollMonthAndPayrollYear(employeeId, month, year)
                    .orElseGet(PayrollTransaction::new);

            payroll.setEmployeeId(employeeId);
            payroll.setSalaryId(salary.getSalaryId());
            payroll.setPayrollMonth(month);
            payroll.setPayrollYear(year);
            payroll.setWorkingDays(workingDays);
            payroll.setPresentDays((int) presentDays);
            payroll.setPaidLeaveDays(paidLeaveDays);
            payroll.setLopDays(lopDays);
            payroll.setGrossEarnings(gross);
            payroll.setTotalDeductions(totalDeductions);
            payroll.setLopDeduction(lopDeduction);
            payroll.setNetPay(netPay);
            payroll.setStatus(PayrollTransaction.PayrollStatus.GENERATED);
            payroll.setGeneratedOn(LocalDateTime.now());

            PayrollTransaction saved = payrollTransactionRepo.save(payroll);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Payroll generated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Generation failed");
            response.setMessage("Could not generate payroll. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<PayrollTransaction> findById(int payrollId) {
        ResponseMessage<PayrollTransaction> response = new ResponseMessage<>();
        try {
            Optional<PayrollTransaction> payroll = payrollTransactionRepo.findById(payrollId);
            if (payroll.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No payroll record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(payroll.get());
            response.setHeader("Success");
            response.setMessage("Payroll record fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the payroll record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<PayrollTransaction>> findByEmployee(int employeeId) {
        ResponseMessage<List<PayrollTransaction>> response = new ResponseMessage<>();
        try {
            List<PayrollTransaction> records = payrollTransactionRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Payroll records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch payroll records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<PayrollTransaction>> findByMonth(int month, int year) {
        ResponseMessage<List<PayrollTransaction>> response = new ResponseMessage<>();
        try {
            List<PayrollTransaction> records = payrollTransactionRepo.findByPayrollMonthAndPayrollYear(month, year);
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Payroll records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch payroll records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<PayrollTransaction>> findAll() {
        ResponseMessage<List<PayrollTransaction>> response = new ResponseMessage<>();
        try {
            List<PayrollTransaction> records = payrollTransactionRepo.findAll();
            response.setResponseOutput(records);
            response.setHeader("Success");
            response.setMessage("Payroll records fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch payroll records. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<PayrollTransaction> markPaid(int payrollId) {
        ResponseMessage<PayrollTransaction> response = new ResponseMessage<>();
        try {
            Optional<PayrollTransaction> existingOpt = payrollTransactionRepo.findById(payrollId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No payroll record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            PayrollTransaction existing = existingOpt.get();
            existing.setStatus(PayrollTransaction.PayrollStatus.PAID);
            existing.setPaidOn(LocalDateTime.now());

            PayrollTransaction saved = payrollTransactionRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Payroll marked as paid.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not mark payroll as paid. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<PayrollTransaction> cancel(int payrollId) {
        ResponseMessage<PayrollTransaction> response = new ResponseMessage<>();
        try {
            Optional<PayrollTransaction> existingOpt = payrollTransactionRepo.findById(payrollId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No payroll record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            PayrollTransaction existing = existingOpt.get();
            existing.setStatus(PayrollTransaction.PayrollStatus.CANCELLED);

            PayrollTransaction saved = payrollTransactionRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Payroll cancelled.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not cancel payroll. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int payrollId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!payrollTransactionRepo.existsById(payrollId)) {
                response.setHeader("Not found");
                response.setMessage("No payroll record found for this id.");
                response.setStatusCode(404);
                return response;
            }
            payrollTransactionRepo.deleteById(payrollId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Payroll record deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the payroll record. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

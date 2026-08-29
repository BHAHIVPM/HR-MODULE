# HR Module — New Tables Added

Added on top of the existing `user_login`, `employee_master`, and `client_details` tables.
Each follows the same Model → Repository → Service → Controller pattern already used for
`EmployeeMaster`, and returns the same `ResponseMessage<T>` wrapper. No SQL scripts are
needed — `spring.jpa.hibernate.ddl-auto=update` (already set in `application.properties`)
will create these tables automatically the next time the app starts.

## 1. Department Master (`department_master`)
Base org unit. `/department`
- POST `/save`, GET `/{id}`, GET `/active`, GET `/all`, PUT `/update/{id}`, DELETE `/{id}`

## 2. Designation Master (`designation_master`)
Job titles, optionally linked to a department. `/designation`
- POST `/save`, GET `/{id}`, GET `/by-department/{departmentId}`, GET `/active`, GET `/all`,
  PUT `/update/{id}`, DELETE `/{id}`

## 3. Holiday Master (`holiday_master`)
Company holiday calendar, scopable to a location. `/holiday`
- POST `/save`, GET `/{id}`, GET `/active`, GET `/year/{year}`, GET `/all`,
  PUT `/update/{id}`, DELETE `/{id}`

## 4. Salary Master (`salary_master`)
Salary structure per employee (earnings + deductions). Gross/net are computed
server-side on every save/update — never trust client-sent totals. `/salary`
- POST `/save`, GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/all`,
  PUT `/update/{id}`, DELETE `/{id}`

## 5. Leave Master (`leave_master`)
Leave *types* (Casual, Sick, Earned...) with default annual entitlement and
carry-forward rules. `/leave-type`
- POST `/save`, GET `/{id}`, GET `/active`, GET `/all`, PUT `/update/{id}`, DELETE `/{id}`

## 6. Leave Application (`leave_application`)
Actual employee leave requests against a leave type, with an approval workflow
(PENDING → APPROVED/REJECTED/CANCELLED). `/leave-application`
- POST `/apply`, GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/pending`, GET `/all`
- PUT `/{id}/approve/{approverEmployeeId}?remarks=...`
- PUT `/{id}/reject/{approverEmployeeId}?remarks=...`
- PUT `/{id}/cancel`
- DELETE `/{id}`

## 7. Attendance (`attendance`)
Daily attendance, one row per employee per day (unique constraint). Includes
convenience check-in/check-out endpoints that auto-compute worked hours. `/attendance`
- POST `/save`, POST `/check-in/{employeeId}`, PUT `/check-out/{employeeId}`
- GET `/{id}`, GET `/by-employee/{employeeId}`,
  GET `/by-employee/{employeeId}/range?from=YYYY-MM-DD&to=YYYY-MM-DD`, GET `/all`
- PUT `/update/{id}`, DELETE `/{id}`

## Notes / how tables relate
- `employee_master.department` / `.designation` are still free-text today. Once you're ready,
  switch those two fields to `Integer departmentId` / `Integer designationId` referencing the
  new masters, so employees can be filtered/reported on by real department/designation records.
- All foreign-key-style links (`employeeId`, `departmentId`, `leaveTypeId`, etc.) are plain
  `Integer` columns, not JPA `@ManyToOne` relationships — consistent with how `EmployeeMaster`
  already references `loginId`/`reportingManagerId`. Keeps things simple and avoids N+1 fetch
  surprises; you can upgrade to real JPA relationships later if needed.
- All new controllers are **not** under `/open/**` or `/guest/**`, so — same as
  `EmployeeMasterController` — they require a full AUTH token per `SecurityConfig`.

## 8. Bank Details (`bank_details`)
Employee bank accounts for salary disbursement. An employee can have multiple accounts on
file; only one can be flagged `isPrimary` at a time — the service automatically un-flags
any previous primary when a new one is saved. `/bank-details`
- POST `/save`, GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/all`,
  PUT `/update/{id}`, DELETE `/{id}`

## 9. Document Master (`document_master`)
Metadata for employee documents (ID proof, offer letter, PAN, Aadhar, resume, etc.).
`filePath` stores wherever the actual file was uploaded to (disk path / S3 key / URL) —
this table only tracks metadata, not the file bytes. `/document`
- POST `/save`, GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/all`
- PUT `/{id}/verify` — marks a document as verified
- PUT `/update/{id}`, DELETE `/{id}`

## 10. Shift Master (`shift_master`)
Reusable shift timing definitions (e.g. "General 9-6", "Night Shift"). `/shift`
- POST `/save`, GET `/{id}`, GET `/active`, GET `/all`, PUT `/update/{id}`, DELETE `/{id}`

## 11. Employee Shift (`employee_shift`)
Maps an employee to a shift with an effective date range, so shift history is preserved
instead of being overwritten when timings change. Assigning a new shift automatically
closes out whichever shift was previously `ACTIVE` for that employee. `/employee-shift`
- POST `/assign`, GET `/{id}`, GET `/current/{employeeId}`, GET `/history/{employeeId}`,
  GET `/all`, DELETE `/{id}`

## 12. Performance Review (`performance_review`)
Appraisal cycles and ratings, with a DRAFT → SUBMITTED → REVIEWED → ACKNOWLEDGED workflow.
`reviewerId` references the reviewing manager (another row in `employee_master`).
`/performance-review`
- POST `/save`, GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/by-reviewer/{reviewerId}`,
  GET `/all`
- PUT `/{id}/submit`, PUT `/{id}/complete?overallRating=4.5&reviewerComments=...`,
  PUT `/{id}/acknowledge`
- PUT `/update/{id}`, DELETE `/{id}`

## 13. Asset Master (`asset_master`)
Company assets (laptop, mobile, ID card, etc.) and who they're issued to.
`issuedToEmployeeId` is null while an asset is unassigned/in stock. `/asset`
- POST `/save`, GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/available`, GET `/all`
- PUT `/{id}/issue/{employeeId}` — issues an AVAILABLE asset
- PUT `/{id}/return?condition=GOOD&remarks=...` — returns an ISSUED asset to the pool
- PUT `/update/{id}`, DELETE `/{id}`

## 14. Payroll Transaction (`payroll_transaction`)
One generated payslip per employee per month/year (unique constraint enforces this).
`PayrollTransactionService.generate()` is the cross-module calculation: it pulls the
employee's active `salary_master` record, counts `attendance` days marked PRESENT/HALF_DAY
in that month, sums `leave_application` days APPROVED and overlapping that month as paid
leave, and treats any remaining working day as Loss-of-Pay (LOP), deducted pro-rata from
gross salary. `/payroll`
- POST `/generate/{employeeId}?month=8&year=2026` — computes (or re-computes) the payslip
- GET `/{id}`, GET `/by-employee/{employeeId}`, GET `/by-month?month=8&year=2026`, GET `/all`
- PUT `/{id}/mark-paid`, PUT `/{id}/cancel`
- DELETE `/{id}`

## How the new tables map to each other
```
employee_master (employeeId)
 ├── department_master (departmentId) ←── designation_master.departmentId
 ├── salary_master.employeeId ──────────► payroll_transaction.salaryId
 ├── attendance.employeeId ─────────────► payroll_transaction (days present)
 ├── leave_master.leaveTypeId ◄───────── leave_application.leaveTypeId
 ├── leave_application.employeeId ──────► payroll_transaction (paid leave days)
 ├── bank_details.employeeId (isPrimary → payroll disbursement account)
 ├── document_master.employeeId
 ├── shift_master.shiftId ◄───────────── employee_shift.shiftId
 ├── employee_shift.employeeId
 ├── performance_review.employeeId (+ .reviewerId → another employeeId)
 └── asset_master.issuedToEmployeeId
```
All links are plain `Integer` columns (not JPA `@ManyToOne`), matching how `EmployeeMaster`
already references `loginId`/`reportingManagerId` — simple, and avoids N+1 fetch surprises.

## All 14 new tables at a glance
| # | Table | Controller base path |
|---|---|---|
| 1 | department_master | `/department` |
| 2 | designation_master | `/designation` |
| 3 | holiday_master | `/holiday` |
| 4 | salary_master | `/salary` |
| 5 | leave_master | `/leave-type` |
| 6 | leave_application | `/leave-application` |
| 7 | attendance | `/attendance` |
| 8 | bank_details | `/bank-details` |
| 9 | document_master | `/document` |
| 10 | shift_master | `/shift` |
| 11 | employee_shift | `/employee-shift` |
| 12 | performance_review | `/performance-review` |
| 13 | asset_master | `/asset` |
| 14 | payroll_transaction | `/payroll` |

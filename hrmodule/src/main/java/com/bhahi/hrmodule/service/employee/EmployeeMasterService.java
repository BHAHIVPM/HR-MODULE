package com.bhahi.hrmodule.service.employee;

import com.bhahi.hr.exception.CustomException;
import com.bhahi.hrmodule.Utils.MobileAndEmailValidation;
import com.bhahi.hrmodule.Utils.UserRolePolicy;
import com.bhahi.hrmodule.dto.auth.UserCreationResponse;
import com.bhahi.hrmodule.model.employee.EmployeeMaster;
import com.bhahi.hrmodule.model.auth.UserLogin;
import com.bhahi.hrmodule.repository.employee.EmployeeMasterRepo;
import com.bhahi.hrmodule.service.auth.UserLoginService;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeMasterService {

    private final EmployeeMasterRepo employeeMasterRepo;
    private final UserLoginService userLoginService;

    @Transactional
    public ResponseMessage<EmployeeMaster> save(EmployeeMaster employee) {
        ResponseMessage<EmployeeMaster> response = new ResponseMessage<>();
        try {
            // Role gate (one-way, no reverse): only ADMIN can create an EMPLOYEE record.
            // USER / EMPLOYEE / AGENT / SUPERADMIN / DEVELOPER callers are rejected here.
            // The nested UserLoginService.save() re-checks ADMIN -> EMPLOYEE as well.
            UserRolePolicy.requireEmployeeCreationAllowed();
            System.err.println("00000===============================11");
            if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
                throw new CustomException("Name missing.", "Please provide the employee first name.", 400);
            }
            if (employee.getDateOfJoining() == null) {
                throw new CustomException("Date missing.", "Please provide the employee date of joining.", 400);
            }System.err.println("00000===============================121");
            MobileAndEmailValidation.mobileNumValidation(employee.getMobileNo());
            MobileAndEmailValidation.emailIdValidation(employee.getEmail());
            String mail = employee.getEmail().trim();
            String mobile = employee.getMobileNo().trim();
            if (employeeMasterRepo.existsByEmail(mail)
                    || employeeMasterRepo.findByEmail(mail).isPresent()) {
                throw new CustomException("Email already exists.", "This email id is already registered.", 409);
            }System.err.println("00000===============================113");
            if (employeeMasterRepo.existsByMobileNo(mobile)
                    || employeeMasterRepo.findByMobileNo(mobile).isPresent()) {
                throw new CustomException("Mobile already exists.", "This mobile number is already registered.", 409);
            }
            employee.setEmail(mail);
            employee.setMobileNo(mobile);
            if (employee.getStatus() == null) {
                employee.setStatus(EmployeeMaster.EmployeeStatus.ACTIVE);
            }
            System.err.println("00000===============================117");
            // Step 1: Save employee with a unique temporary employeeCode.
            // employeeCode is NOT NULL + UNIQUE, so we cannot save NULL, and a fixed
            // "TEMP" would collide on the 2nd concurrent save. UUID makes it unique.
            employee.setEmployeeCode("TMP-" + java.util.UUID.randomUUID());
            System.err.println("-----------------89");
            EmployeeMaster saved = employeeMasterRepo.save(employee);
            System.err.println("00000===============================118");
            // Step 2: Generate employeeCode based on the auto-generated employeeId
            // Format: EMP000001, EMP000012, EMP000326, etc. (EMP + 6-digit zero-padded id)
            String employeeCode = String.format("EMP%06d", saved.getEmployeeId());
            saved.setEmployeeCode(employeeCode);
            saved = employeeMasterRepo.save(saved);
            System.err.println("00000===============================1108");
            // Step 3: Register the employee as a UserLogin with EMPLOYEE user type
            UserLogin userLogin = new UserLogin();
            userLogin.setName(saved.getFirstName() + " " + (saved.getLastName() != null ? saved.getLastName() : ""));
            userLogin.setUserMail(saved.getEmail());
            userLogin.setMobileNo(saved.getMobileNo());
            userLogin.setUserType(UserLogin.UserType.EMPLOYEE);

            ResponseMessage<UserCreationResponse> userResponse = userLoginService.save(userLogin);

            if (userResponse.getStatusCode() == 200) {System.err.println("00000===============================1132");
                // Step 4: Get the generated loginId and update EmployeeMaster
                String generatedLoginId = userResponse.getResponseOutput().user().getUserId();
                saved.setLoginId(generatedLoginId);
                saved = employeeMasterRepo.save(saved);
            } else {
                // Keep both tables in sync: if the login row failed (role/duplicate/login-id),
                // roll the employee row back instead of leaving an orphan.
                throw new CustomException(userResponse.getHeader(), userResponse.getMessage(),
                        userResponse.getStatusCode());
            }

            // Clear sensitive fields before returning
            saved.setLoginId(saved.getLoginId());

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Employee registered successfully with employee code: " + employeeCode);
            response.setStatusCode(200);
            return response;
        } catch (CustomException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            System.err.println("--------------------------------------9999");
            response.setHeader(e.getHeader());
            response.setMessage(e.getMessage());
            response.setStatusCode(e.getStatusCode());
            return response;
        } catch (DataIntegrityViolationException e) {
            System.err.println("====================================23532523");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setHeader("Duplicate entry");
            response.setMessage("Email or mobile number already exists.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            System.err.println("===================================sdg=23532523");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setHeader("Save failed");
            response.setMessage("Could not save the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<EmployeeMaster> findById(int employeeId) {
        ResponseMessage<EmployeeMaster> response = new ResponseMessage<>();
        try {
            Optional<EmployeeMaster> employee = employeeMasterRepo.findById(employeeId);
            if (employee.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No employee found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(employee.get());
            response.setHeader("Success");
            response.setMessage("Employee fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeMaster>> findAllActive() {
        ResponseMessage<List<EmployeeMaster>> response = new ResponseMessage<>();
        try {
            List<EmployeeMaster> employees = employeeMasterRepo.findByStatus(EmployeeMaster.EmployeeStatus.ACTIVE);
            response.setResponseOutput(employees);
            response.setHeader("Success");
            response.setMessage("Active employees fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch employees. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<EmployeeMaster>> findAll() {
        ResponseMessage<List<EmployeeMaster>> response = new ResponseMessage<>();
        try {
            List<EmployeeMaster> employees = employeeMasterRepo.findAll();
            response.setResponseOutput(employees);
            response.setHeader("Success");
            response.setMessage("Employees fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch employees. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // employeeCode intentionally left out of the copy - treated as immutable once assigned.
    public ResponseMessage<EmployeeMaster> update(int employeeId, EmployeeMaster updates) {
        ResponseMessage<EmployeeMaster> response = new ResponseMessage<>();
        try {
            Optional<EmployeeMaster> existingOpt = employeeMasterRepo.findById(employeeId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No employee found for this id.");
                response.setStatusCode(404);
                return response;
            }

            EmployeeMaster existing = existingOpt.get();
            if (updates.getFirstName() != null) {
                existing.setFirstName(updates.getFirstName());
            }
            if (updates.getLastName() != null) {
                existing.setLastName(updates.getLastName());
            }
            if (updates.getEmail() != null) {
                MobileAndEmailValidation.emailIdValidation(updates.getEmail());
                String mail = updates.getEmail().trim();
                Optional<EmployeeMaster> owner = employeeMasterRepo.findByEmail(mail);
                if (owner.isPresent() && owner.get().getEmployeeId() != existing.getEmployeeId()) {
                    throw new CustomException("Email already exists.", "This email id is already registered.", 409);
                }
                existing.setEmail(mail);
            }
            if (updates.getMobileNo() != null) {
                MobileAndEmailValidation.mobileNumValidation(updates.getMobileNo());
                String mobile = updates.getMobileNo().trim();
                Optional<EmployeeMaster> owner = employeeMasterRepo.findByMobileNo(mobile);
                if (owner.isPresent() && owner.get().getEmployeeId() != existing.getEmployeeId()) {
                    throw new CustomException("Mobile already exists.", "This mobile number is already registered.", 409);
                }
                existing.setMobileNo(mobile);
            }
            if (updates.getDateOfBirth() != null) {
                existing.setDateOfBirth(updates.getDateOfBirth());
            }
            if (updates.getDateOfJoining() != null) {
                existing.setDateOfJoining(updates.getDateOfJoining());
            }
            if (updates.getDepartment() != null) {
                existing.setDepartment(updates.getDepartment());
            }
            if (updates.getDesignation() != null) {
                existing.setDesignation(updates.getDesignation());
            }
            if (updates.getReportingManagerId() != null) {
                existing.setReportingManagerId(updates.getReportingManagerId());
            }
            if (updates.getStatus() != null) {
                existing.setStatus(updates.getStatus());
            }

            EmployeeMaster saved = employeeMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Employee updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (CustomException e) {
            response.setHeader(e.getHeader());
            response.setMessage(e.getMessage());
            response.setStatusCode(e.getStatusCode());
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate entry");
            response.setMessage("Email or mobile number already exists.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int employeeId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!employeeMasterRepo.existsById(employeeId)) {
                response.setHeader("Not found");
                response.setMessage("No employee found for this id.");
                response.setStatusCode(404);
                return response;
            }
            employeeMasterRepo.deleteById(employeeId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Employee deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the employee. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

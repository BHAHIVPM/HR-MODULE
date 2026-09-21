package com.bhahi.hrmodule.service.auth;

import com.bhahi.hr.exception.CustomException;
import com.bhahi.hrmodule.Utils.MobileAndEmailValidation;
import com.bhahi.hrmodule.Utils.UserRolePolicy;
import com.bhahi.hrmodule.Utils.VerhoeffUtils;
import com.bhahi.hrmodule.dto.auth.UserCreationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bhahi.hrmodule.model.auth.UserLogin;
import com.bhahi.hrmodule.repository.auth.UserLoginRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepo userLoginRepo;
    private final PasswordEncoder passwordEncoder;
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public ResponseMessage<UserCreationResponse> save(UserLogin userLogin) {
        ResponseMessage<UserCreationResponse> response = new ResponseMessage<>();
        try {
            UserRolePolicy.requireCreatableTarget(userLogin.getUserType());
            MobileAndEmailValidation.mobileNumValidation(userLogin.getMobileNo());
            MobileAndEmailValidation.emailIdValidation(userLogin.getUserMail());
            if (userLogin.getName() == null || userLogin.getName().trim().isEmpty()) {
                throw new CustomException("Name missing.", "Please provide the user name.", 400);
            }
            String mail = userLogin.getUserMail().trim();
            String mobile = userLogin.getMobileNo().trim();
            if (userLoginRepo.existsByUserMail(mail)) {
                throw new CustomException("Email already exists.", "This email id is already registered.", 409);
            }
            if (userLoginRepo.existsByMobileNo(mobile)) {
                throw new CustomException("Mobile already exists.", "This mobile number is already registered.", 409);
            }
            userLogin.setUserMail(mail);
            userLogin.setMobileNo(mobile);
            String loginId = generateLoginId(userLogin.getUserType());
            userLogin.setUserId(loginId);
            String rawTempPassword = generateTempPassword();
            userLogin.setTempPassword(passwordEncoder.encode(rawTempPassword));
            userLogin.setPassword(null);
            userLogin.setStatus(UserLogin.UserStatus.ACTIVE);
            UserLogin saved = userLoginRepo.save(userLogin);
            saved.setPassword(null);
            saved.setTempPassword(null);
            response.setResponseOutput(new UserCreationResponse(saved, rawTempPassword));
            response.setHeader("Success");
            response.setMessage("User created. Share the temp password now - it will not be shown again.");
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
            response.setHeader("Save failed");
            response.setMessage("Could not save the user. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
    private String generateLoginId(UserLogin.UserType userType) {
        String currentLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentLoginId == null || currentLoginId.length() < 4) {
            throw new IllegalStateException("Current user login id is not available or invalid.");
        }
        String clientId = currentLoginId.substring(0, 4);
        String typeCode = String.format("%02d", userType.getTypeCode());
        String prefix = clientId + typeCode;
        Optional<Integer> maxCounterOpt = userLoginRepo.findMaxCounterByPrefix(prefix);
        int nextCounter = maxCounterOpt.map(c -> c + 1).orElse(1);
        String counter = String.format("%05d", nextCounter);
        String first11Digits = prefix + counter;
        char checkDigit = VerhoeffUtils.generateCheckDigit(first11Digits);
        return first11Digits + checkDigit;
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(TEMP_PASSWORD_CHARS.charAt(RANDOM.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    public ResponseMessage<List<UserLogin>> findAll() {
        ResponseMessage<List<UserLogin>> response = new ResponseMessage<>();
        try {
            List<UserLogin> users = userLoginRepo.findAll();
            users.forEach(u -> { u.setPassword(null); u.setTempPassword(null); });
            response.setResponseOutput(users);
            response.setHeader("Success");
            response.setMessage("Users fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch users. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
    public ResponseMessage<UserLogin> update(String userId, UserLogin updates) {
        ResponseMessage<UserLogin> response = new ResponseMessage<>();
        try {
            Optional<UserLogin> existingOpt = userLoginRepo.findByUserId(userId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No user found for this login id.");
                response.setStatusCode(404);
                return response;
            }
            UserLogin existing = existingOpt.get();
            if (updates.getUserType() != null && updates.getUserType() != existing.getUserType()) {
                throw new CustomException("Forbidden.",
                        "User type cannot be changed here. It would bypass the creation hierarchy.", 403);
            }
            if (updates.getName() != null) {
                existing.setName(updates.getName());
            }
            if (updates.getUserMail() != null) {
                MobileAndEmailValidation.emailIdValidation(updates.getUserMail());
                String mail = updates.getUserMail().trim();
                Optional<UserLogin> owner = userLoginRepo.findByUserMail(mail);
                if (owner.isPresent() && !owner.get().getUserId().equals(existing.getUserId())) {
                    throw new CustomException("Email already exists.", "This email id is already registered.", 409);
                }
                existing.setUserMail(mail);
            }
            if (updates.getMobileNo() != null) {
                MobileAndEmailValidation.mobileNumValidation(updates.getMobileNo());
                String mobile = updates.getMobileNo().trim();
                Optional<UserLogin> mOwner = userLoginRepo.findByMobileNo(mobile);
                if (mOwner.isPresent() && !mOwner.get().getUserId().equals(existing.getUserId())) {
                    throw new CustomException("Mobile already exists.", "This mobile number is already registered.", 409);
                }
                existing.setMobileNo(mobile);
            }
            if (updates.getStatus() != null) {
                existing.setStatus(updates.getStatus());
            }
            UserLogin saved = userLoginRepo.save(existing);
            saved.setPassword(null);
            saved.setTempPassword(null);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("User updated successfully.");
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
            response.setMessage("Could not update the user. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(String userId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!userLoginRepo.existsByUserId(userId)) {
                response.setHeader("Not found");
                response.setMessage("No user found for this login id.");
                response.setStatusCode(404);
                return response;
            }
            userLoginRepo.deleteByUserId(userId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("User deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the user. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}

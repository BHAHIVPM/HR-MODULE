package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.Utils.VerhoeffUtils;
import com.bhahi.hrmodule.dto.UserCreationResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bhahi.hrmodule.model.UserLogin;
import com.bhahi.hrmodule.repository.UserLoginRepo;
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

    // Creating a user no longer takes a real password - it issues a one-time temp password instead.
// The user (or whoever's setting them up) must call verify-temp-password then change-password
// on /auth before the account can log in normally.
    public ResponseMessage<UserCreationResponse> save(UserLogin userLogin) {

        ResponseMessage<UserCreationResponse> response = new ResponseMessage<>();
        try {
            // Auto-generate the 12-digit login id:
            //   digits 1-4  = client id (first 4 digits of the current authenticated user's login id)
            //   digits 5-6  = user type code (DEVELOPER=10, SUPERADMIN=11, ADMIN=12, USER=13, EMPLOYEE=14, AGENT=15)
            //   digits 7-11 = 5-digit sequential counter per client+type
            //   digit  12   = Verhoeff checksum digit over the first 11 digits
            String loginId = generateLoginId(userLogin.getUserType());
            userLogin.setUserId(loginId);

            String rawTempPassword = generateTempPassword();

            userLogin.setTempPassword(passwordEncoder.encode(rawTempPassword));
            userLogin.setPassword(null); // no real password until change-password is called
            userLogin.setStatus(UserLogin.UserStatus.ACTIVE);

            UserLogin saved = userLoginRepo.save(userLogin);
            saved.setPassword(null);
            saved.setTempPassword(null); // never echo hashes back, even here

            response.setResponseOutput(new UserCreationResponse(saved, rawTempPassword));
            response.setHeader("Success");
            response.setMessage("User created. Share the temp password now - it will not be shown again.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the user. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // ==============================================================================================================
    // LOGIN ID GENERATION
    // 12-digit format: [4-digit clientId][2-digit typeCode][5-digit counter][1-digit Verhoeff check]
    // ==============================================================================================================
    private String generateLoginId(UserLogin.UserType userType) {

        // 1. First 4 digits = client id of the CURRENT authenticated user
        String currentLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentLoginId == null || currentLoginId.length() < 4) {
            throw new IllegalStateException("Current user login id is not available or invalid.");
        }
        String clientId = currentLoginId.substring(0, 4);

        // 2. Next 2 digits = user type code
        String typeCode = String.format("%02d", userType.getTypeCode());

        // 3. Build the 6-digit prefix for looking up existing users of this client+type
        String prefix = clientId + typeCode;

        // 4. Get the previous counter for this prefix (digits 7-11 of existing login ids)
        Optional<Integer> maxCounterOpt = userLoginRepo.findMaxCounterByPrefix(prefix);
        int nextCounter = maxCounterOpt.map(c -> c + 1).orElse(1);

        // 5. Format the counter as 5 digits (padded with leading zeros)
        String counter = String.format("%05d", nextCounter);

        // 6. First 11 digits = clientId + typeCode + counter
        String first11Digits = prefix + counter;

        // 7. Last digit = Verhoeff checksum over the first 11 digits
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

    // Only userName, userMail, mobileNo, userType, status are editable here.
// password/tempPassword deliberately excluded - those change only via the /auth endpoints.
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
            existing.setName(updates.getName());
            // userId is auto-generated and immutable — do NOT allow changing it here
            existing.setUserMail(updates.getUserMail());
            existing.setMobileNo(updates.getMobileNo());
            existing.setUserType(updates.getUserType());
            existing.setStatus(updates.getStatus());

            UserLogin saved = userLoginRepo.save(existing);
            saved.setPassword(null);
            saved.setTempPassword(null);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("User updated successfully.");
            response.setStatusCode(200);
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

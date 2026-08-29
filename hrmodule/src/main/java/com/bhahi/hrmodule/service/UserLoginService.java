package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.dto.UserCreationResponse;
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
            existing.setUserId(updates.getUserId());
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

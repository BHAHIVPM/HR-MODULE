package com.bhahi.hrmodule.service.auth;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.exception.CustomException;
import com.bhahi.hrmodule.Utils.AESUtils;
import com.bhahi.hrmodule.Utils.MobileAndEmailValidation;
import com.bhahi.hrmodule.Utils.UserRolePolicy;
import com.bhahi.hrmodule.Utils.VerhoeffUtils;
import com.bhahi.hrmodule.dto.auth.UserCreationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bhahi.hrmodule.model.auth.UserLogin;
import com.bhahi.hrmodule.repository.auth.UserLoginRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hr.dto.OTP.OtpMailEvent;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepo userLoginRepo;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final KafkaEventPublisher kafkaEventPublisher;

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final int MAX_OTP_ATTEMPTS = 5;

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

    // ---------------------------------------------------------------
    // OTP
    // ---------------------------------------------------------------

    private String otpKey(String loginId, String mobileNo) {
        return "otp:" + loginId + ":" + mobileNo;
    }

    private String otpAttemptsKey(String loginId, String mobileNo) {
        return "otp-attempts:" + loginId + ":" + mobileNo;
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***" + email.substring(Math.max(at, 0));
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    public ResponseMessage<String> generateOtp(String loginId, String mobileNo) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (loginId == null || loginId.isBlank() || mobileNo == null || mobileNo.isBlank()) {
                throw new CustomException("Missing data.", "Login id and mobile number are required.", 400);
            }
            loginId = loginId.trim();
            mobileNo = mobileNo.trim();

            UserLogin user = userLoginRepo.findByUserId(loginId).orElse(null);
            // Same message for "no user" and "wrong mobile" so nobody can probe which login ids exist
            if (user == null || user.getMobileNo() == null || !user.getMobileNo().trim().equals(mobileNo)) {
                throw new CustomException("Invalid details.", "Login id and mobile number do not match.", 400);
            }
            if (user.getStatus() != UserLogin.UserStatus.ACTIVE) {
                throw new CustomException("Forbidden.", "This user is not active.", 403);
            }
            String email = user.getUserMail();
            if (email == null || email.isBlank()) {
                throw new CustomException("No email.", "No email id is registered for this user.", 400);
            }

            String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
            String encryptedOtp = AESUtils.encrypt(otp);

            redisTemplate.opsForValue().set(otpKey(loginId, mobileNo), encryptedOtp, OTP_TTL);
            redisTemplate.delete(otpAttemptsKey(loginId, mobileNo)); // reset attempts for the new OTP

            // Never return the OTP in the API response.
            // Fire and forget: the mail server picks this event up from Kafka and sends the mail.
            // We do not wait for Kafka or for the mail. If publishing fails, the OTP is discarded.
            final String redisKey = otpKey(loginId, mobileNo);
            kafkaEventPublisher.publish(
                    KafkaEventType.OTP_MAIL,
                    loginId,
                    new OtpMailEvent(loginId, email.trim(), user.getName(), otp, (int) OTP_TTL.toMinutes()),
                    () -> redisTemplate.delete(redisKey));

            response.setResponseOutput("OTP sent.");
            response.setHeader("Success");
            response.setMessage("OTP is being sent to " + maskEmail(email.trim()) + ". It is valid for "
                    + OTP_TTL.toMinutes() + " minutes.");
            response.setStatusCode(200);
            return response;
        } catch (CustomException e) {
            response.setHeader(e.getHeader());
            response.setMessage(e.getMessage());
            response.setStatusCode(e.getStatusCode());
            return response;
        } catch (Exception e) {
            response.setHeader("OTP failed");
            response.setMessage("Could not generate the OTP. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> verifyOtp(String loginId, String mobileNo, String otp) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (loginId == null || loginId.isBlank() || mobileNo == null || mobileNo.isBlank()
                    || otp == null || otp.isBlank()) {
                throw new CustomException("Missing data.", "Login id, mobile number and OTP are required.", 400);
            }
            loginId = loginId.trim();
            mobileNo = mobileNo.trim();
            otp = otp.trim();

            String key = otpKey(loginId, mobileNo);
            String attemptsKey = otpAttemptsKey(loginId, mobileNo);

            String stored = redisTemplate.opsForValue().get(key);
            if (stored == null) {
                throw new CustomException("OTP expired.", "OTP has expired or was not requested. Please request a new one.", 400);
            }

            // Limit wrong guesses (a 6-digit OTP is easy to brute-force otherwise)
            Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
            if (attempts != null && attempts == 1) {
                redisTemplate.expire(attemptsKey, OTP_TTL);
            }
            if (attempts != null && attempts > MAX_OTP_ATTEMPTS) {
                redisTemplate.delete(key);
                redisTemplate.delete(attemptsKey);
                throw new CustomException("Too many attempts.", "Too many wrong attempts. Please request a new OTP.", 429);
            }

            String realOtp = AESUtils.decrypt(stored);
            boolean matches = MessageDigest.isEqual(
                    realOtp.getBytes(StandardCharsets.UTF_8),
                    otp.getBytes(StandardCharsets.UTF_8));
            if (!matches) {
                throw new CustomException("Invalid OTP.", "The OTP you entered is incorrect.", 400);
            }

            // One-time use
            redisTemplate.delete(key);
            redisTemplate.delete(attemptsKey);

            response.setResponseOutput("Verified.");
            response.setHeader("Success");
            response.setMessage("OTP verified successfully.");
            response.setStatusCode(200);
            return response;
        } catch (CustomException e) {
            response.setHeader(e.getHeader());
            response.setMessage(e.getMessage());
            response.setStatusCode(e.getStatusCode());
            return response;
        } catch (Exception e) {
            response.setHeader("Verify failed");
            response.setMessage("Could not verify the OTP. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
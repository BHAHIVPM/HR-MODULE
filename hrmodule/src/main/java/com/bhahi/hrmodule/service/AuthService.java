package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.jwt.JwtUtils;
import com.bhahi.hrmodule.model.UserLogin;
import com.bhahi.hrmodule.repository.UserLoginRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("\\d{12}");

    private final UserLoginRepo userLoginRepo;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    // ==============================================================================================================
    // FULL AUTH LOGIN — loginId (12 digit) + password -> Access_token cookie (AUTH type, full API access)
    // NOTE: by the time this runs, DbRoutingPreAuthFilter has already read the first 4 digits of the
    // loginId path variable and pointed the RoutingDataSource at the correct tenant DB for this request.
    // ==============================================================================================================
    public ResponseMessage<String> login(String userId, String rawPassword, HttpServletResponse response) {

        ResponseMessage<String> result = new ResponseMessage<>();

        ResponseMessage<String> invalid = validateLoginId(userId);
        if (invalid != null) {
            return invalid;
        }

        try {
            Optional<UserLogin> userOpt = userLoginRepo.findByUserId(userId);
            if (userOpt.isEmpty()) {
                return unauthorized(result);
            }

            UserLogin user = userOpt.get();

            if (user.getStatus() != UserLogin.UserStatus.ACTIVE) {
                result.setHeader("Account inactive");
                result.setMessage("This account is inactive. Please contact your administrator.");
                result.setStatusCode(403);
                return result;
            }

            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                return unauthorized(result);
            }

            String logId = UUID.randomUUID().toString();
            String token = jwtUtils.generateToken(user, logId);

            setTokenCookie(response, "Access_token", token);

            result.setResponseOutput("Login successful.");
            result.setHeader("Success");
            result.setMessage("Login successful.");
            result.setStatusCode(200);
            return result;

        } catch (Exception e) {
            result.setHeader("Login failed");
            result.setMessage("Something went wrong while logging in.");
            result.setStatusCode(500);
            return result;
        }
    }

    // ==============================================================================================================
    // GUEST TOKEN — userId (12 digit) only, no password -> Guest_Token cookie (GUEST type, basic API access only)
    // ==============================================================================================================
    public ResponseMessage<String> guestToken(String userId, HttpServletResponse response) {

        ResponseMessage<String> result = new ResponseMessage<>();

        ResponseMessage<String> invalid = validateLoginId(userId);
        if (invalid != null) {
            return invalid;
        }

        try {
            String token = jwtUtils.generateGuestToken(userId);
            setTokenCookie(response, "Guest_Token", token);

            result.setResponseOutput("Guest token issued.");
            result.setHeader("Success");
            result.setMessage("Guest token issued.");
            result.setStatusCode(200);
            return result;

        } catch (Exception e) {
            result.setHeader("Guest token failed");
            result.setMessage("Something went wrong while issuing the guest token.");
            result.setStatusCode(500);
            return result;
        }
    }

    // ==============================================================================================================
    // HELPERS
    // ==============================================================================================================
    private ResponseMessage<String> validateLoginId(String userId) {
        if (userId == null || !LOGIN_ID_PATTERN.matcher(userId).matches()) {
            ResponseMessage<String> result = new ResponseMessage<>();
            result.setHeader("Invalid login id");
            result.setMessage("Login id must be exactly 12 digits (first 4 digits identify the client).");
            result.setStatusCode(400);
            return result;
        }
        return null;
    }

    private ResponseMessage<String> unauthorized(ResponseMessage<String> result) {
        result.setHeader("Invalid credentials");
        result.setMessage("Login id or password is incorrect.");
        result.setStatusCode(401);
        return result;
    }

    private void setTokenCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);           // requires HTTPS in real deployments; fine for local http too in most browsers during dev, but flip to false if you test on plain http and it gets rejected
        cookie.setPath("/");
        cookie.setMaxAge(jwtUtils.getJwtTokenExpire() / 1000); // jwtTokenExpire is in ms, cookie maxAge is in seconds
        response.addCookie(cookie);
    }

    // ==============================================================================================================
// VERIFY TEMP PASSWORD — loginId + tempPassword -> confirms the temp password issued at creation is correct.
// This is the "prove who you are" step before changePassword, since a new account has no real password yet.
// ==============================================================================================================
    public ResponseMessage<String> verifyTempPassword(String userId, String rawTempPassword) {

        ResponseMessage<String> result = new ResponseMessage<>();

        ResponseMessage<String> invalid = validateLoginId(userId);
        if (invalid != null) {
            return invalid;
        }

        try {
            Optional<UserLogin> userOpt = userLoginRepo.findByUserId(userId);
            if (userOpt.isEmpty()) {
                return unauthorized(result);
            }

            UserLogin user = userOpt.get();

            if (user.getTempPassword() == null || !passwordEncoder.matches(rawTempPassword, user.getTempPassword())) {
                result.setHeader("Invalid temp password");
                result.setMessage("Temp password is incorrect or has already been used.");
                result.setStatusCode(401);
                return result;
            }

            result.setResponseOutput("Temp password verified.");
            result.setHeader("Success");
            result.setMessage("Temp password verified. You can now set a new password.");
            result.setStatusCode(200);
            return result;

        } catch (Exception e) {
            result.setHeader("Verification failed");
            result.setMessage("Something went wrong while verifying the temp password.");
            result.setStatusCode(500);
            return result;
        }
    }

    // ==============================================================================================================
// CHANGE PASSWORD — loginId + tempPassword + newPassword -> sets the real password column.
// Re-checks the temp password here too rather than trusting a prior verifyTempPassword call, since these
// are two separate stateless requests. Clears tempPassword afterwards so it can't be replayed.
// ==============================================================================================================
    public ResponseMessage<String> changePassword(String userId, String rawTempPassword, String newPassword) {

        ResponseMessage<String> result = new ResponseMessage<>();

        ResponseMessage<String> invalid = validateLoginId(userId);
        if (invalid != null) {
            return invalid;
        }

        try {
            Optional<UserLogin> userOpt = userLoginRepo.findByUserId(userId);
            if (userOpt.isEmpty()) {
                return unauthorized(result);
            }

            UserLogin user = userOpt.get();

            if (user.getTempPassword() == null || !passwordEncoder.matches(rawTempPassword, user.getTempPassword())) {
                result.setHeader("Invalid temp password");
                result.setMessage("Temp password is incorrect or has already been used.");
                result.setStatusCode(401);
                return result;
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setTempPassword(null); // one-time use only
            userLoginRepo.save(user);

            result.setResponseOutput("Password changed.");
            result.setHeader("Success");
            result.setMessage("Password changed successfully. You can now log in with your new password.");
            result.setStatusCode(200);
            return result;

        } catch (Exception e) {
            result.setHeader("Change password failed");
            result.setMessage("Something went wrong while changing the password.");
            result.setStatusCode(500);
            return result;
        }
    }
}

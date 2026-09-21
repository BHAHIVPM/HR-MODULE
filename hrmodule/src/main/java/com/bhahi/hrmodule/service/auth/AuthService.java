package com.bhahi.hrmodule.service.auth;

import com.bhahi.hrmodule.dto.auth.AboutMeResponse;
import com.bhahi.hrmodule.jwt.JwtUtils;
import com.bhahi.hrmodule.model.auth.UserLogin;
import com.bhahi.hrmodule.repository.auth.UserLoginRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    // REFRESH TOKEN — reads the existing Access_token cookie, validates it, extracts the same claims,
    // and issues a freshly generated token with the same info back into the Access_token cookie.
    // NOTE: like login/guest-token, the loginId is in the URL path so DbRoutingPreAuthFilter can point
    // this request at the correct tenant DB before the @RequestBody is parsed.
    // ==============================================================================================================
    public ResponseMessage<String> refreshToken(String userId, HttpServletRequest request, HttpServletResponse response) {

        ResponseMessage<String> result = new ResponseMessage<>();

        ResponseMessage<String> invalid = validateLoginId(userId);
        if (invalid != null) {
            return invalid;
        }

        try {
            // 1. Check the existing Access_token cookie exists
            String existingToken = getAccessTokenFromCookies(request);
            if (existingToken == null) {
                result.setHeader("Token missing");
                result.setMessage("Access token not found. Please log in again.");
                result.setStatusCode(401);
                return result;
            }

            // 2. Check the existing token has not expired
            if (jwtUtils.isTokenExpired(existingToken)) {
                result.setHeader("Token expired");
                result.setMessage("Access token has expired. Please log in again.");
                result.setStatusCode(401);
                return result;
            }

            // 3. Extract claims from the existing token
            Claims claims = jwtUtils.extractType(existingToken);

            // 4. Only AUTH tokens can be refreshed (guest tokens are not refreshable)
            String tokenType = claims.get("type", String.class);
            if (!"AUTH".equals(tokenType)) {
                result.setHeader("Invalid token type");
                result.setMessage("Only AUTH tokens can be refreshed.");
                result.setStatusCode(401);
                return result;
            }

            // 5. Verify the token subject matches the loginId in the URL path
            String tokenSubject = claims.getSubject();
            if (tokenSubject == null || !userId.equals(tokenSubject)) {
                result.setHeader("Token mismatch");
                result.setMessage("Token does not match the login id.");
                result.setStatusCode(401);
                return result;
            }

            // 6. Look up the user to get fresh details from the DB
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

            // 7. Reuse the same logId from the existing token and regenerate a fresh token
            String logId = claims.get("logId", String.class);
            String newToken = jwtUtils.generateToken(user, logId);

            // 8. Set the refreshed token back as the Access_token cookie
            setTokenCookie(response, "Access_token", newToken);

            result.setResponseOutput(newToken);
            result.setHeader("Success");
            result.setMessage("Token refreshed successfully.");
            result.setStatusCode(200);
            return result;

        } catch (Exception e) {
            result.setHeader("Refresh failed");
            result.setMessage("Something went wrong while refreshing the token.");
            result.setStatusCode(500);
            return result;
        }
    }

    // ==============================================================================================================
    // AUTH-ME — checks the CURRENT token (Access_token cookie or Authorization: Bearer header).
    // Valid   -> 200 + true. Missing / invalid / expired -> 401 + false.
    // Kept permitAll in SecurityConfig on purpose so THIS method (not the filter chain)
    // decides the 401 body, keeping the ResponseMessage shape consistent.
    // ==============================================================================================================
    public ResponseMessage<Boolean> authMe(HttpServletRequest request) {
        ResponseMessage<Boolean> result = new ResponseMessage<>();
        String token = getTokenFromRequest(request);
        if (token == null) {
            result.setResponseOutput(false);
            result.setHeader("Unauthorized");
            result.setMessage("Access token is missing. Please log in again.");
            result.setStatusCode(401);
            return result;
        }
        try {
            if (jwtUtils.isTokenExpired(token)) {
                result.setResponseOutput(false);
                result.setHeader("Unauthorized");
                result.setMessage("Access token has expired. Please log in again.");
                result.setStatusCode(401);
                return result;
            }
            // Signature / structure check - throws on tampered or malformed tokens.
            jwtUtils.extractType(token);
        } catch (Exception e) {
            result.setResponseOutput(false);
            result.setHeader("Unauthorized");
            result.setMessage("Access token is invalid. Please log in again.");
            result.setStatusCode(401);
            return result;
        }
        result.setResponseOutput(true);
        result.setHeader("Success");
        result.setMessage("Token is valid.");
        result.setStatusCode(200);
        return result;
    }

    // ==============================================================================================================
    // ABOUT-ME — same token check as authMe, then returns the profile behind the token:
    // userId, name, email, mobileNo, userType, status. Never passwords / otp.
    // Missing / invalid / expired token -> 401. Unknown user -> 401. Inactive -> 403.
    // ==============================================================================================================
    public ResponseMessage<AboutMeResponse> aboutMe(HttpServletRequest request) {
        ResponseMessage<AboutMeResponse> result = new ResponseMessage<>();
        String token = getTokenFromRequest(request);
        if (token == null) {
            result.setHeader("Unauthorized");
            result.setMessage("Access token is missing. Please log in again.");
            result.setStatusCode(401);
            return result;
        }
        final String userId;
        try {
            if (jwtUtils.isTokenExpired(token)) {
                result.setHeader("Unauthorized");
                result.setMessage("Access token has expired. Please log in again.");
                result.setStatusCode(401);
                return result;
            }
            userId = jwtUtils.extractUsername(token);
        } catch (Exception e) {
            result.setHeader("Unauthorized");
            result.setMessage("Access token is invalid. Please log in again.");
            result.setStatusCode(401);
            return result;
        }
        try {
            Optional<UserLogin> userOpt = userLoginRepo.findByUserId(userId);
            if (userOpt.isEmpty()) {
                result.setHeader("Unauthorized");
                result.setMessage("User not found for this token. Please log in again.");
                result.setStatusCode(401);
                return result;
            }
            UserLogin user = userOpt.get();
            if (user.getStatus() != UserLogin.UserStatus.ACTIVE) {
                result.setHeader("Account inactive");
                result.setMessage("This account is inactive. Please contact your administrator.");
                result.setStatusCode(403);
                return result;
            }
            result.setResponseOutput(AboutMeResponse.from(user));
            result.setHeader("Success");
            result.setMessage("Profile fetched successfully.");
            result.setStatusCode(200);
            return result;
        } catch (Exception e) {
            result.setHeader("Fetch failed");
            result.setMessage("Something went wrong while fetching the profile.");
            result.setStatusCode(500);
            return result;
        }
    }

    // Looks for the token in: Access_token cookie -> Guest_Token cookie -> Authorization: Bearer header.
    private String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String guest = null;
            for (Cookie cookie : cookies) {
                if (cookie == null || cookie.getName() == null) {
                    continue;
                }
                if ("Access_token".equals(cookie.getName()) && cookie.getValue() != null
                        && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
                if ("Guest_Token".equals(cookie.getName()) && cookie.getValue() != null
                        && !cookie.getValue().isBlank()) {
                    guest = cookie.getValue();
                }
            }
            if (guest != null) {
                return guest;
            }
        }
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String bearer = header.substring(7).trim();
            return bearer.isEmpty() ? null : bearer;
        }
        return null;
    }
    private String getAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("Access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

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

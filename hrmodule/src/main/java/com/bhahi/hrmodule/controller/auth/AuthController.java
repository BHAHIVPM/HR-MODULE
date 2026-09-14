package com.bhahi.hrmodule.controller.auth;

import com.bhahi.hrmodule.dto.auth.ChangePasswordRequest;
import com.bhahi.hrmodule.dto.auth.LoginRequest;
import com.bhahi.hrmodule.dto.auth.VerifyTempPasswordRequest;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // loginId is in the URL path (not the body) on purpose - DbRoutingPreAuthFilter reads it
    // straight off the URI to route to the right tenant DB before any @RequestBody is parsed.
    @PostMapping("/login/{loginId}")
    public ResponseEntity<ResponseMessage<String>> login(@PathVariable String loginId,
                                                           @Valid @RequestBody LoginRequest request,
                                                           HttpServletResponse response) {
        ResponseMessage<String> result = authService.login(loginId, request.password(), response);
        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/guest-token/{loginId}")
    public ResponseEntity<ResponseMessage<String>> guestToken(@PathVariable String loginId,
                                                                HttpServletResponse response) {
        ResponseMessage<String> result = authService.guestToken(loginId, response);
        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/refresh-token/{loginId}")
    public ResponseEntity<ResponseMessage<String>> refreshToken(@PathVariable String loginId,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) {
        ResponseMessage<String> result = authService.refreshToken(loginId, request, response);
        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/verify-temp-password/{loginId}")
    public ResponseEntity<ResponseMessage<String>> verifyTempPassword(@PathVariable String loginId,
                                                                      @Valid @RequestBody VerifyTempPasswordRequest request) {
        ResponseMessage<String> result = authService.verifyTempPassword(loginId, request.tempPassword());
        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/change-password/{loginId}")
    public ResponseEntity<ResponseMessage<String>> changePassword(@PathVariable String loginId,
                                                                  @Valid @RequestBody ChangePasswordRequest request) {
        ResponseMessage<String> result = authService.changePassword(loginId, request.tempPassword(), request.newPassword());
        return ResponseEntity.status(result.getStatusCode()).body(result);
    }
}

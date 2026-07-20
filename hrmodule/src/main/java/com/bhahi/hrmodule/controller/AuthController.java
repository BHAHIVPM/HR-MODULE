package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.dto.LoginRequest;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.AuthService;
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
}

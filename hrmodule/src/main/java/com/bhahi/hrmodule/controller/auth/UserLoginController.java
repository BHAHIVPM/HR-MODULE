package com.bhahi.hrmodule.controller.auth;

import com.bhahi.hrmodule.dto.auth.UserCreationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import com.bhahi.hrmodule.model.auth.UserLogin;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.UserLoginService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userData")
public class UserLoginController {

    private final UserLoginService userLoginService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<UserCreationResponse>> save(@RequestBody UserLogin login) {
        ResponseMessage<UserCreationResponse> response = userLoginService.save(login);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<UserLogin>>> findAll() {
        ResponseMessage<List<UserLogin>> response = userLoginService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{loginId}")
    public ResponseEntity<ResponseMessage<UserLogin>> update(@PathVariable String loginId,
                                                             @RequestBody UserLogin updates) {
        ResponseMessage<UserLogin> response = userLoginService.update(loginId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{loginId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable String loginId) {
        ResponseMessage<String> response = userLoginService.delete(loginId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

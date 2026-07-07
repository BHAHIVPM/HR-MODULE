package com.bhahi.hrmodule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bhahi.hrmodule.model.UserLogin;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.UserLoginService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/userData")
public class UserLoginController {
    
    private final UserLoginService userLoginService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<UserLogin>> save(@RequestBody UserLogin login){
        ResponseMessage<UserLogin> response = new ResponseMessage<>();
        try{
        response=userLoginService.save(login);
        return ResponseEntity.status(response.getStatusCode()).body(response);
        }catch(Exception e){
        return ResponseEntity.status(response.getStatusCode()).body(response);
        }
        
    }
}

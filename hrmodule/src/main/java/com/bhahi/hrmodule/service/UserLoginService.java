package com.bhahi.hrmodule.service;

import org.springframework.stereotype.Service;

import com.bhahi.hrmodule.model.UserLogin;
import com.bhahi.hrmodule.repository.UserLoginRepo;
import com.bhahi.hrmodule.response.ResponseMessage;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginService{
    
    private final UserLoginRepo userLoginRepo;


    public ResponseMessage<UserLogin> save(UserLogin userLogin){
        
        ResponseMessage<UserLogin> response=new ResponseMessage<>();
        try{
        UserLogin login=userLoginRepo.save(userLogin);
        response.setResponseOutput(login);
        response.setHeader("Success");
        response.setMessage("The user data is inserted successfully.");
        response.setStatusCode(200);
        return response;
        }catch(Exception e){
            System.err.println(e);
        return response;
        }
    }

    public ResponseMessage<UserLogin> findById(String loginId){

        ResponseMessage<UserLogin> response=new ResponseMessage<>();
        try{
            Optional<UserLogin> login=userLoginRepo.findByUserCode(loginId);
            response.setResponseOutput(login.get());
            response.setHeader("Succeess");
            response.setMessage("The user data is inserted successfully.");
            response.setStatusCode(200);
            return response;
        }catch(Exception e){

            System.err.println(e);
            return response;
        }
    }


} 
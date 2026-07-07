package com.bhahi.hrmodule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class UserLogin {

    @Id
    private int userId;
    @Column(length = 15, nullable = false)
    @NotNull
    private String userCode;
    @NotNull
    @Column( nullable = false)
    private String userName;
    @NotNull    
    @Column(length = 150, nullable = false)
    private String userMail;
    @NotNull
    @Column(length = 10, nullable = false)
    private String mobileNo;
    private String password;
    @NotNull
    private enum  UserStatus{Active, Inactive};
    @Column(length = 10, nullable = false)
    private UserStatus status; 
    @NotNull
    private String otp;
    
    
}

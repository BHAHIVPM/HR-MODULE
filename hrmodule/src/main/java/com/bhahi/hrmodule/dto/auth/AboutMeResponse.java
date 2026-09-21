package com.bhahi.hrmodule.dto.auth;

import com.bhahi.hrmodule.model.auth.UserLogin;

// Safe profile view for /auth/about-me - no password / tempPassword / otp / tableId.
public record AboutMeResponse(
        String userId,
        String name,
        String email,
        String mobileNo,
        UserLogin.UserType userType,
        UserLogin.UserStatus status) {

    public static AboutMeResponse from(UserLogin user) {
        return new AboutMeResponse(
                user.getUserId(),
                user.getName(),
                user.getUserMail(),
                user.getMobileNo(),
                user.getUserType(),
                user.getStatus());
    }
}

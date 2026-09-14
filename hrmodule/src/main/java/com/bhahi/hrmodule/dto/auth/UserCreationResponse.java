package com.bhahi.hrmodule.dto.auth;

import com.bhahi.hrmodule.model.auth.UserLogin;

// Returned only once, at creation time - the raw temp password is never stored or shown again.
public record UserCreationResponse(UserLogin user, String tempPassword) {}
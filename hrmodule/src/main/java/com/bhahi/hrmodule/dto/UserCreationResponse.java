package com.bhahi.hrmodule.dto;

import com.bhahi.hrmodule.model.UserLogin;

// Returned only once, at creation time - the raw temp password is never stored or shown again.
public record UserCreationResponse(UserLogin user, String tempPassword) {}
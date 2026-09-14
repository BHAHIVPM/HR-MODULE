package com.bhahi.hrmodule.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@NotBlank String tempPassword, @NotBlank String newPassword) {}
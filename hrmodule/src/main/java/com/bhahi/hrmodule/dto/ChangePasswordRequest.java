package com.bhahi.hrmodule.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@NotBlank String tempPassword, @NotBlank String newPassword) {}
package com.bhahi.hrmodule.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyTempPasswordRequest(@NotBlank String tempPassword) {}
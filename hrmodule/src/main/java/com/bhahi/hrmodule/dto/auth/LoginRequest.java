package com.bhahi.hrmodule.dto.auth;

import jakarta.validation.constraints.NotBlank;

// Password is sent in the request body, never in the URL/path,
// since path/URIs tend to get logged (access logs, proxies, browser history).
public record LoginRequest(@NotBlank String password) {
}

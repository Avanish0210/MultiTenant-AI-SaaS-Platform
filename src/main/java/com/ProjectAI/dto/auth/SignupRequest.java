package com.ProjectAI.dto.auth;

public record SignupRequest(
        String email,
        String name,
        String password
) {
}

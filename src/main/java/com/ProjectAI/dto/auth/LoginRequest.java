package com.ProjectAI.dto.auth;

public record LoginRequest(
        String email ,
        String password
) {
}

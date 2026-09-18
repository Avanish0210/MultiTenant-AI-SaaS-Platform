package com.ProjectAI.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfileResponse user
) {
}

package com.ProjectAI.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}

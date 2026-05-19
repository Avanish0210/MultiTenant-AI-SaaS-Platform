package com.ProjectAI.service;

import com.ProjectAI.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {
    @Nullable UserProfileResponse getProfile(Long userId);
}

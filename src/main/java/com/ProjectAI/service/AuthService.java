package com.ProjectAI.service;

import com.ProjectAI.dto.auth.AuthResponse;
import com.ProjectAI.dto.auth.LoginRequest;
import com.ProjectAI.dto.auth.SignupRequest;
import com.ProjectAI.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    @Nullable AuthResponse signup(SignupRequest signupRequest);

    @Nullable AuthResponse login(LoginRequest loginRequest);


}


package com.ProjectAI.service;

import com.ProjectAI.dto.auth.AuthResponse;
import com.ProjectAI.dto.auth.LoginRequest;
import com.ProjectAI.dto.auth.SignupRequest;
import com.ProjectAI.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    AuthResponse signup(SignupRequest signupRequest);

    AuthResponse login(LoginRequest loginRequest);


}


package com.ProjectAI.service.impl;

import com.ProjectAI.dto.auth.UserProfileResponse;
import com.ProjectAI.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}

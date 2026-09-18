package com.ProjectAI.service.impl;

import com.ProjectAI.dto.auth.UserProfileResponse;
import com.ProjectAI.entity.User;
import com.ProjectAI.error.ResourceNotFoundException;
import com.ProjectAI.repository.UserRepository;
import com.ProjectAI.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("user",userId.toString()));
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getName()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("User" , username));
    }
}

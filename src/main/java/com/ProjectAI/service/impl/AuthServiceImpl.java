package com.ProjectAI.service.impl;

import com.ProjectAI.dto.auth.AuthResponse;
import com.ProjectAI.dto.auth.LoginRequest;
import com.ProjectAI.dto.auth.SignupRequest;
import com.ProjectAI.dto.auth.UserProfileResponse;
import com.ProjectAI.entity.User;
import com.ProjectAI.error.BadRequestException;
import com.ProjectAI.error.ResourceNotFoundException;
import com.ProjectAI.mapper.UserMapper;
import com.ProjectAI.repository.UserRepository;
import com.ProjectAI.security.AuthUtil;
import com.ProjectAI.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest signupRequest) {
        userRepository.findByUsername(signupRequest.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username: "+signupRequest.username());
        });
        User user = userMapper.toEntity(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        user  = userRepository.save(user);

        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = authUtil.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken , userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username() ,loginRequest.password() )
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = authUtil.generateAccessToken(user);
        String refreshToken = authUtil.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken , userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        Long userId = authUtil.verifyAccessToken(refreshToken).userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(refreshToken , userId.toString()));

        String newAccessToken = authUtil.generateAccessToken(user);
        String newRefreshToken = authUtil.generateRefreshToken(user);
        UserProfileResponse userProfileResponse = new UserProfileResponse(userId, user.getUsername(), user.getName());

        return new AuthResponse(newAccessToken, newRefreshToken, userProfileResponse);
    }
}

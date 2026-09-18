package com.ProjectAI.controller;

import com.ProjectAI.dto.auth.AuthResponse;
import com.ProjectAI.dto.auth.LoginRequest;
import com.ProjectAI.dto.auth.SignupRequest;
import com.ProjectAI.dto.auth.UserProfileResponse;
import com.ProjectAI.security.AuthUtil;
import com.ProjectAI.service.AuthService;
import com.ProjectAI.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final AuthUtil authUtil;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.signup(signupRequest);
        response.addCookie(createRefreshTokenCookie(authResponse.refreshToken()));
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);
        response.addCookie(createRefreshTokenCookie(authResponse.refreshToken()));
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        response.addCookie(createRefreshTokenCookie(null));
        return ResponseEntity.ok("success");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AuthenticationServiceException("Invalid refreshToken");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Invalid refreshToken"));

        AuthResponse authResponse = authService.refreshToken(refreshToken);
        response.addCookie(createRefreshTokenCookie(authResponse.refreshToken()));

        return ResponseEntity.ok(authResponse);
    }

    private Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setMaxAge(token == null ? 0 : 60 * 60 * 24 * 7);
        return cookie;
    }
}

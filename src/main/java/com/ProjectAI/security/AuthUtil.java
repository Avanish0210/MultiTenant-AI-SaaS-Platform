package com.ProjectAI.security;

import com.ProjectAI.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId" , user.getId().toString())
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(getSecretKey())
                .compact();
    }

    public JwtUserPrincipal verifyAccessToken(String token){
        Claims claims = parseAndValidateToken(token, "access");

        Long userId = Long.parseLong(claims.get("userId" , String.class));
        String username = claims.getSubject();
        return new JwtUserPrincipal(userId , username , new ArrayList<>());
    }

    public JwtUserPrincipal verifyRefreshToken(String token){
        Claims claims = parseAndValidateToken(token, "refresh");

        Long userId = Long.parseLong(claims.get("userId" , String.class));
        String username = claims.getSubject();
        return new JwtUserPrincipal(userId , username , new ArrayList<>());
    }

    private Claims parseAndValidateToken(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tokenType = claims.get("tokenType", String.class);
        if (!expectedType.equals(tokenType)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token type");
        }

        return claims;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof JwtUserPrincipal userPrincipal)) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        return userPrincipal.userId();
    }


    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("tokenType", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L *60*60*24*30*6))
                .signWith(getSecretKey())
                .compact();
    }
}

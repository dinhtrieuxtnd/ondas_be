package com.example.ondas_be.presentation.controller;

import com.example.ondas_be.application.dto.request.LoginDto;
import com.example.ondas_be.application.dto.response.AuthResponseDto;
import com.example.ondas_be.domain.entity.User;
import com.example.ondas_be.domain.repoport.UserRepoPort;
import com.example.ondas_be.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepoPort userRepoPort;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginDto dto) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        if (!auth.isAuthenticated()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userRepoPort.findByEmail(dto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        return new AuthResponseDto(token);
    }
}

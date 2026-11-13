package com.example.application_management.security.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}

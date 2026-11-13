package com.example.application_management.security.dto;

import com.example.application_management.security.enums.UserRole;

public record RegisterResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {}

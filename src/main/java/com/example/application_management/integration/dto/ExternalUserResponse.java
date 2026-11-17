package com.example.application_management.integration.dto;

// DTO untuk menyimpan response daripada external API.
// Java record menyediakan constructor dan getter automatik.
public record ExternalUserResponse(
        Long id,
        String name,
        String username,
        String email,
        String phone,
        String website
) {
}

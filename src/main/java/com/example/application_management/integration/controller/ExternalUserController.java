
package com.example.application_management.integration.controller;

import com.example.application_management.integration.dto.ExternalUserResponse;
import com.example.application_management.integration.service.ExternalUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integrations")
public class ExternalUserController {

    private final ExternalUserService externalUserService;

    // Constructor injection untuk Service
    public ExternalUserController(
            ExternalUserService externalUserService) {

        this.externalUserService = externalUserService;
    }

    // API yang akan dipanggil melalui Postman
    // Contoh: GET /api/integrations/users/1
    @GetMapping("/users/{userId}")
    public ResponseEntity<ExternalUserResponse> getExternalUser(
            @PathVariable Long userId) {

        // Panggil Service untuk mendapatkan data external API
        ExternalUserResponse response = externalUserService.getExternalUser(userId);

        // Return HTTP 200 bersama data user
        return ResponseEntity.ok(response);
    }
}

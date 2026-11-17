
package com.example.application_management.integration.service;

import com.example.application_management.integration.dto.ExternalUserResponse;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExternalUserService {

    // RestClient digunakan untuk memanggil external REST API
    private final RestClient restClient;

    // Constructor untuk setup URL external API
    public ExternalUserService(RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    // Method untuk mendapatkan user daripada external API
    public ExternalUserResponse getExternalUser(Long userId) {

        // Hantar HTTP GET request kepada JSONPlaceholder
        ExternalUserResponse response = restClient.get()

                // Contoh URL: /users/1
                .uri("/users/{id}", userId)

                // Execute request dan terima response
                .retrieve()

                // Handle jika external API return 4xx atau 5xx
                .onStatus(
                        HttpStatusCode::isError,
                        (request, externalResponse) -> {
                            throw new ResponseStatusException(
                                    org.springframework.http.HttpStatus.BAD_GATEWAY,
                                    "External API returned status: "
                                            + externalResponse.getStatusCode()
                            );
                        }
                )

                // Convert JSON response kepada DTO
                .body(ExternalUserResponse.class);

        // Pastikan response body tidak kosong
        if (response == null) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_GATEWAY,
                    "External API returned an empty response"
            );
        }

        // Return data kepada Controller
        return response;
    }
}

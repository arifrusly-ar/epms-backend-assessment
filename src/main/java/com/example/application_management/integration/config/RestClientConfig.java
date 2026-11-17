
package com.example.application_management.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Daftarkan RestClient.Builder sebagai Spring Bean.
    // Spring boleh inject bean ini ke ExternalUserService.
    @Bean
    public RestClient.Builder restClientBuilder() {

        // Builder digunakan untuk membina HTTP client
        // yang akan memanggil third-party API.
        return RestClient.builder();
    }
}

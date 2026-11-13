package com.example.application_management.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Enable CORS integration dalam Spring Security.
                // Spring akan menggunakan CorsConfigurationSource
                // yang kita define di bawah.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF untuk JWT-based stateless API.
                // Nota: Ini sesuai dengan architecture Bearer token
                // yang tidak bergantung pada session cookie.
                .csrf(csrf -> csrf.disable())

                // Stateless: Spring Security tidak menyimpan
                // authentication dalam HTTP session.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Configure endpoint access.
                .authorizeHttpRequests(auth -> auth

                        // Benarkan CORS preflight request.
                        // OPTIONS request tidak memerlukan JWT.
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Register dan login boleh diakses
                        // tanpa JWT authentication.
                        .requestMatchers(
                                "/api/auth/**",
                                "/h2-console/**"
                        )
                        .permitAll()

                        // Endpoint lain memerlukan authentication.
                        .anyRequest().authenticated()
                )

                // Kekalkan JWT Resource Server authentication.
                // JWT daripada Angular akan divalidasi oleh Spring.
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )

                // Kekalkan H2 Console frame configuration.
                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.sameOrigin()
                        )
                );

        return http.build();
    }


    /**
     * CORS CONFIGURATION
     *
     * Membenarkan Angular frontend pada localhost:4200
     * menghantar HTTP requests kepada Spring Boot API.
     *
     * Configuration ini untuk local development.
     * Production perlu menggunakan frontend domain sebenar.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        // Create CORS configuration.
        CorsConfiguration configuration =
                new CorsConfiguration();

        // Hanya benarkan Angular development origin.
        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        // HTTP methods yang dibenarkan.
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Benarkan request headers yang diperlukan.
        //
        // Authorization: Bearer JWT
        // Content-Type: application/json
        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        // Tidak menggunakan cross-origin cookies
        // kerana EPMS menggunakan Bearer JWT.
        configuration.setAllowCredentials(false);

        // Browser boleh cache preflight result
        // selama 3600 saat.
        configuration.setMaxAge(3600L);

        // Apply CORS rules kepada semua endpoints.
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public KeyPair jwtKeyPair() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);

        return generator.generateKeyPair();
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {

        RSAKey rsaKey = new RSAKey.Builder(
                (RSAPublicKey) jwtKeyPair.getPublic())

                .privateKey(
                        (RSAPrivateKey) jwtKeyPair.getPrivate()
                ).build();

        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeypair) {

        return NimbusJwtDecoder
                .withPublicKey(
                        (RSAPublicKey) jwtKeypair.getPublic()
                ).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return jwtConverter;
    }

}

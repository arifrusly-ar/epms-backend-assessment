package com.example.application_management.security.service;

import com.example.application_management.security.dto.LoginResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse generateToken(Authentication authentication) {

        Instant now = Instant.now();
        long expirySeconds = 3600;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("epms")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirySeconds))
                .subject(authentication.getName())
                .claim(
                        "roles",
                        authentication.getAuthorities().stream()
                                .map(a -> a.getAuthority())
                                .toList()
                )
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        ).getTokenValue();

//        JwsHeader jwsHeader = JwsHeader
//                .with(SignatureAlgorithm.RS256)
//                .build();
//
//        String token = jwtEncoder.encode(
//                JwtEncoderParameters.from(jwsHeader, claims)
//        ).getTokenValue();

        return new LoginResponse(
                token,
                "Bearer",
                expirySeconds
        );
    }
}
package com.example.application_management.security.service;

import com.example.application_management.security.dto.LoginRequest;
import com.example.application_management.security.dto.LoginResponse;
import com.example.application_management.security.dto.RegisterRequest;
import com.example.application_management.security.dto.RegisterResponse;
import com.example.application_management.security.entity.AppUser;
import com.example.application_management.security.enums.UserRole;
import com.example.application_management.security.repository.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (appUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        AppUser user = new AppUser();

        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.ENGINEER);
        user.setEnabled(true);

        AppUser savedUser = appUserRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email().trim().toLowerCase(),
                                request.password()
                        ));

        return jwtService.generateToken(authentication);

    }
}

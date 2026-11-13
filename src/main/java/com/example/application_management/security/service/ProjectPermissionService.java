package com.example.application_management.security.service;

import com.example.application_management.member.enums.ProjectRole;
import com.example.application_management.member.repository.ProjectMemberRepository;
import com.example.application_management.security.entity.AppUser;
import com.example.application_management.security.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ProjectPermissionService {

    private final AppUserRepository userRepository;
    private final ProjectMemberRepository memberRepository;

    public ProjectPermissionService(AppUserRepository userRepository,
                                    ProjectMemberRepository memberRepository) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    public boolean canApprove(Long projectId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        AppUser user = userRepository.findByEmail(authentication.getName()).orElse(null);

        if (user == null || !user.isEnabled()) {
            return false;
        }

        return memberRepository.existsByProject_IdAndAppUser_IdAndRole(
                projectId, user.getId(), ProjectRole.PROJECT_MANAGER
        );
    }
}

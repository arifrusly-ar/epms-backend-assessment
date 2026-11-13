package com.example.application_management.audit.service;

import com.example.application_management.audit.entity.DocumentAudit;
import com.example.application_management.audit.enums.AuditAction;
import com.example.application_management.audit.repository.DocumentAuditRepository;
import com.example.application_management.document.entity.Document;
import com.example.application_management.security.entity.AppUser;
import com.example.application_management.security.repository.AppUserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DocumentAuditService {

    private final DocumentAuditRepository auditRepository;
    private final AppUserRepository userRepository;

    public DocumentAuditService(
            DocumentAuditRepository auditRepository,
            AppUserRepository userRepository
    ) {
        this.auditRepository = auditRepository;
        this.userRepository = userRepository;
    }

    public void record(
            Document document,
            AuditAction action,
            String remarks
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "Authenticated user is required"
            );
        }

        String email = authentication.getName();

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        DocumentAudit audit = new DocumentAudit();

        audit.setDocument(document);
        audit.setPerformedBy(user);
        audit.setAction(action);
        audit.setRemarks(remarks);

        auditRepository.save(audit);
    }
}
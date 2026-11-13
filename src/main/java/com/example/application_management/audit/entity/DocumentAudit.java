package com.example.application_management.audit.entity;

import com.example.application_management.audit.enums.AuditAction;
import com.example.application_management.document.entity.Document;
import com.example.application_management.security.entity.AppUser;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "document_audit")
public class DocumentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private AppUser performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private Instant performedAt;

    @Column(length = 1000)
    private String remarks;

    @PrePersist
    public void prePersist() {
        if (performedAt == null) {
            performedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public AppUser getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(AppUser performedBy) {
        this.performedBy = performedBy;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Instant performedAt) {
        this.performedAt = performedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

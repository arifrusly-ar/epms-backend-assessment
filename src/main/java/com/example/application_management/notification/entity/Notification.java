package com.example.application_management.notification.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    private Long documentId;

    private String message;

    private Instant createdAt;

    protected Notification() {
    }

    public Notification(
            Long projectId,
            Long documentId,
            String message
    ) {
        this.projectId = projectId;
        this.documentId = documentId;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getMessage() {
        return message;
    }
}
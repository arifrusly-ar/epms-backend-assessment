package com.example.application_management.document.entity;

import com.example.application_management.document.enums.DocumentStatus;
import com.example.application_management.project.entity.Project;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "documents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_document_project_no",
                        columnNames = {"project_id", "document_no"}
                )
        },
        indexes = {
                @Index(name ="idx_document_project_status_created",
                columnList = "project_id, status, created_at")
        }
)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_no", nullable = false)
    private String documentNo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Version
    @Column(nullable = false)
    private Long version;

    public Document() {
    }

    public Long getId() {
        return id;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getVersion() {
        return version;
    }
}

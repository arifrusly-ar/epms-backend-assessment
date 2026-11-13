package com.example.application_management.document.dto;

import com.example.application_management.document.enums.DocumentStatus;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String documentNo;
    private String title;
    private String documentType;
    private DocumentStatus status;
    private LocalDateTime createdAt;

    private Long projectId;
    private String projectNo;

    private Integer currentRevision;
    private String fileName;

    private Long version;

    public DocumentResponse(
            Long id,
            String documentNo,
            String title,
            String documentType,
            DocumentStatus status,
            LocalDateTime createdAt,
            Long projectId,
            String projectNo,
            Integer currentRevision,
            String fileName,
            Long version) {

        this.id = id;
        this.documentNo = documentNo;
        this.title = title;
        this.documentType = documentType;
        this.status = status;
        this.createdAt = createdAt;
        this.projectId = projectId;
        this.projectNo = projectNo;
        this.currentRevision = currentRevision;
        this.fileName = fileName;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public String getTitle() {
        return title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public Integer getCurrentRevision() {
        return currentRevision;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getVersion() {
        return version;
    }
}
package com.example.application_management.document.dto;

import java.time.LocalDateTime;

public class DocumentRevisionResponse {

    private Long id;
    private Integer revisionNo;
    private String fileName;
    private String filePath;
    private String remarks;
    private LocalDateTime createdAt;

    public DocumentRevisionResponse(
            Long id,
            Integer revisionNo,
            String fileName,
            String filePath,
            String remarks,
            LocalDateTime createdAt) {

        this.id = id;
        this.revisionNo = revisionNo;
        this.fileName = fileName;
        this.filePath = filePath;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
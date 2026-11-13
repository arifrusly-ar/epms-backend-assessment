package com.example.application_management.document.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentRevisionRequest {

    @NotBlank(message = "File name is required")
    private String fileName;

    private String filePath;

    private String remarks;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

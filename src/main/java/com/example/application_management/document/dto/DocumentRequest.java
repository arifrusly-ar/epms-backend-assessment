package com.example.application_management.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DocumentRequest {

    @NotBlank(message = "Document number is required")
    @Size(max = 50, message = "Document number cannot exceed 50 characters")
    private String documentNo;

    @NotBlank(message = "Document title is required")
    @Size(max = 200, message= "Document title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "File name is required")
    private String fileName;

    private String filePath;

    private String remarks;

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

package com.example.application_management.document.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "document_revisions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_document_revision",
                        columnNames = {"document_id", "revision_no"}
                )
        }
)
public class DocumentRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "revision_no", nullable = false)
    private Integer revisionNo;

    @Column(nullable = false)
    private String fileName;

    private String filePath;

    private String remarks;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    public DocumentRevision() {

    }

    public Long getId() {
        return id;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}

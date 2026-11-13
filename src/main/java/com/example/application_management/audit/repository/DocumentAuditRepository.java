package com.example.application_management.audit.repository;

import com.example.application_management.audit.entity.DocumentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAuditRepository extends JpaRepository<DocumentAudit,Long> {

    List<DocumentAudit> findByDocumentIdOrderByPerformedAtDescIdDesc(Long documentId);
}

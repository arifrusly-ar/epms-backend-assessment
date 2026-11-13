package com.example.application_management.document.repository;

import com.example.application_management.document.entity.Document;
import com.example.application_management.document.entity.DocumentRevision;
import com.example.application_management.document.enums.DocumentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    boolean existsByProjectIdAndDocumentNo(Long projectId, String documentNo);

    List<Document> findByProjectId(Long projectId);

    Optional<Document> findByIdAndProjectId(Long documentId, Long projectId);

    Page<Document> findByProjectId(Long projectId, Pageable pageable);

    Page<Document> findByProjectIdAndStatus(Long projectId, DocumentStatus status, Pageable pageable);

    //Pessimistic Locking
    //@Lock(PESSIMISTIC_WRITE) meminta database mengenakan write lock pada record.
    //@Query mencari document berdasarkan ID dan project.
    //Optional<Document> digunakan kerana document mungkin tidak wujud.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT d
    FROM Document d
    WHERE d.id = :documentId
      AND d.project.id = :projectId
    """)
    Optional<Document> findByIdAndProjectIdForUpdate(
            @Param("documentId") Long documentId,
            @Param("projectId") Long projectId
    );
}

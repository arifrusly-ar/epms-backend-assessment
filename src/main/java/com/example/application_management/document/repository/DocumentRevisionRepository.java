package com.example.application_management.document.repository;

import com.example.application_management.document.entity.DocumentRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRevisionRepository extends JpaRepository<DocumentRevision, Long> {

    List<DocumentRevision> findByDocumentIdOrderByRevisionNoAsc(Long documentId);

    List<DocumentRevision>findByDocumentIdOrderByRevisionNoDesc(Long documentId);

    //find latest revision
    Optional<DocumentRevision> findFirstByDocumentIdOrderByRevisionNoDesc(Long documentId);

    //batch retrieval --> resolve n+1 query
    @Query("""
    SELECT r
    FROM DocumentRevision r
    WHERE r.document.id IN :documentIds
    AND r.revisionNo = (
        SELECT MAX(r2.revisionNo)
        FROM DocumentRevision r2
        WHERE r2.document.id = r.document.id)""")
    List<DocumentRevision> findLatestRevisionsByDocumentIds(@Param("documentIds") List<Long> documentIds);
}

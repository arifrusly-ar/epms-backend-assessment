package com.example.application_management.document.controller;

import com.example.application_management.document.dto.*;
import com.example.application_management.document.enums.DocumentStatus;
import com.example.application_management.document.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(@PathVariable Long projectId,
                                                           @Valid @RequestBody DocumentRequest request) {

        DocumentResponse document = documentService.createDocument(projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PostMapping("/{documentId}/submit")
    public ResponseEntity<DocumentResponse> submitDocument(@PathVariable Long projectId,
                                                           @PathVariable Long documentId) {

        return ResponseEntity.ok(documentService.submitDocument(projectId, documentId));

    }

    //@PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    @PreAuthorize(
            "hasAnyRole('PROJECT_MANAGER', 'ADMIN') " +
                    "and @projectPermission.canApprove(#projectId, authentication)"
    )
    @PostMapping("/{documentId}/approve")
    public ResponseEntity<DocumentResponse> approveDocument(@PathVariable Long projectId,
                                                            @PathVariable Long documentId) {

        return ResponseEntity.ok(documentService.approveDocument(projectId, documentId));
    }

    //@PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    @PreAuthorize(
            "hasAnyRole('PROJECT_MANAGER', 'ADMIN') " +
                    "and @projectPermission.canApprove(#projectId, authentication)"
    )
    @PostMapping("/{documentId}/reject")
    public ResponseEntity<DocumentResponse> rejectDocument(@PathVariable Long projectId,
                                                           @PathVariable Long documentId) {

        return ResponseEntity.ok(documentService.rejectDocument(projectId,documentId));
    }

    @PostMapping("/{documentId}/request-revision")
    public ResponseEntity<DocumentResponse> requestRevision(@PathVariable Long projectId,
                                                            @PathVariable Long documentId) {

        return ResponseEntity.ok(documentService.requestRevision(projectId, documentId));
    }

    @PostMapping("/api/{documentId}/revisions")
    public ResponseEntity<DocumentResponse> createRevision(@PathVariable Long projectId,
                                                           @PathVariable Long documentId,
                                                           @Valid @RequestBody DocumentRevisionRequest request) {

        DocumentResponse response = documentService.createRevision(projectId, documentId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{documentId}/revisions")
    public ResponseEntity<List<DocumentRevisionResponse>> getRevisionHistory(
            @PathVariable Long projectId, @PathVariable Long documentId) {

        return ResponseEntity.ok(documentService.getRevisionHistory(projectId, documentId));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long projectId,
                                                            @PathVariable Long documentId) {

        return ResponseEntity.ok(documentService.getDocumentById(projectId, documentId));
    }

//    @GetMapping
//    public ResponseEntity<Page<DocumentResponse>> getDocumentsByProject(@PathVariable Long projectId,
//                                                                        Pageable pageable) {
//
//        return ResponseEntity.ok(documentService.getDocumentByProject(projectId, pageable));
//
//    }

//    @GetMapping
//    public ResponseEntity<Page<DocumentResponse>> getDocumentsByProject(@PathVariable Long projectId,
//            @RequestParam(required = false) DocumentStatus status, Pageable pageable) {
//
//        return ResponseEntity.ok(documentService.getDocumentsByProject(projectId, status,
//                pageable));
//    }


    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getDocumentsByProject(

            // ID project yang ingin mendapatkan senarai documents
            @PathVariable Long projectId,

            // Optional filter berdasarkan document status
            // Contoh: DRAFT, SUBMITTED, APPROVED
            @RequestParam(required = false) DocumentStatus status,

            // Page bermula daripada 0
            // page=0 bermaksud page pertama
            @RequestParam(defaultValue = "0") int page) {

        // Pastikan page tidak menerima nombor negatif
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        // Tetapkan maksimum 10 records bagi setiap page
        // Susun mengikut ID supaya pagination konsisten
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));

        // Panggil service yang sudah mempunyai
        // @Transactional(readOnly = true)
        Page<DocumentResponse> documents =
                documentService.getDocumentsByProject(projectId, status, pageable);

        // Return paginated result kepada client
        return ResponseEntity.ok(documents);
    }


    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> updateDocument(@PathVariable Long projectId,
                                                           @PathVariable Long documentId,
                                                           @Valid @RequestBody DocumentUpdateRequest request) {

        return ResponseEntity.ok(documentService.updateDocument(projectId, documentId, request));
    }
}

package com.example.application_management.document.controller;

import com.example.application_management.document.dto.*;
import com.example.application_management.document.enums.DocumentStatus;
import com.example.application_management.document.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
            "hasAnyRole('PROJECT_MANAGER', 'ADMIN')" +
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

    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getDocumentsByProject(@PathVariable Long projectId,
            @RequestParam(required = false) DocumentStatus status, Pageable pageable) {

        return ResponseEntity.ok(documentService.getDocumentsByProject(projectId, status,
                pageable));
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> updateDocument(@PathVariable Long projectId,
                                                           @PathVariable Long documentId,
                                                           @Valid @RequestBody DocumentUpdateRequest request) {

        return ResponseEntity.ok(documentService.updateDocument(projectId, documentId, request));
    }
}

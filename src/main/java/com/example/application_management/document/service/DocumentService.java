package com.example.application_management.document.service;

import com.example.application_management.audit.enums.AuditAction;
import com.example.application_management.audit.service.DocumentAuditService;
import com.example.application_management.document.dto.*;
import com.example.application_management.document.entity.Document;
import com.example.application_management.document.entity.DocumentRevision;
import com.example.application_management.document.enums.DocumentStatus;
import com.example.application_management.document.exception.DocumentNotFoundException;
import com.example.application_management.document.exception.DuplicateDocumentException;
import com.example.application_management.document.exception.InvalidDocumentStateException;
import com.example.application_management.document.repository.DocumentRepository;
import com.example.application_management.document.repository.DocumentRevisionRepository;
import com.example.application_management.kafka.outbox.OutboxService;
import com.example.application_management.kafka.producer.DocumentEventProducer;
import com.example.application_management.project.entity.Project;
import com.example.application_management.project.enums.ProjectStatus;
import com.example.application_management.project.exception.InvalidProjectStateException;
import com.example.application_management.project.exception.ProjectNotFoundException;
import com.example.application_management.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
//import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentRevisionRepository documentRevisionRepository;
    private final ProjectRepository projectRepository;
    private final DocumentAuditService documentAuditService;
    private final DocumentEventProducer documentEventProducer;
    private final OutboxService outboxService;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentRevisionRepository documentRevisionRepository,
                           ProjectRepository projectRepository,
                           DocumentAuditService documentAuditService,
                           DocumentEventProducer documentEventProducer,
                           OutboxService outboxService) {
        this.documentRepository = documentRepository;
        this.documentRevisionRepository = documentRevisionRepository;
        this.projectRepository = projectRepository;
        this.documentAuditService = documentAuditService;
        this.documentEventProducer = documentEventProducer;
        this.outboxService = outboxService;
    }

    @Transactional
    public DocumentResponse createDocument(Long projectId, DocumentRequest request) {

        //1. find project
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException(projectId));

        //2. Business rule
        if (ProjectStatus.COMPLETED == project.getStatus()) {
            throw new InvalidProjectStateException("Cannot create document for completed project");
        }

        //3. Duplicate document validation
        if (documentRepository.existsByProjectIdAndDocumentNo(projectId, request.getDocumentNo())) {
            throw new DuplicateDocumentException(request.getDocumentNo());
        }

        //4. Create Document
        Document document = new Document();

        document.setDocumentNo(request.getDocumentNo());
        document.setTitle(request.getTitle());
        document.setDocumentType(request.getDocumentType());
        document.setStatus(DocumentStatus.DRAFT);
        document.setCreatedAt(LocalDateTime.now());
        document.setProject(project);

        Document savedDocument = documentRepository.save(document);

        //inject auditserivce for audit record
        documentAuditService.record(
                document,
                AuditAction.CREATED,
                "Document created"
        );

        //5. Create initial revision
        DocumentRevision revision = new DocumentRevision();

        revision.setRevisionNo(0);
        revision.setFileName(request.getFileName());
        revision.setFilePath(request.getFilePath());
        revision.setRemarks(request.getRemarks());
        revision.setCreatedAt(LocalDateTime.now());

        revision.setDocument(savedDocument);

        DocumentRevision savedDocumentRevision = documentRevisionRepository.save(revision);

        //6. Response
        return mapToResponse(savedDocument, savedDocumentRevision);
    }

    //submit document
    @Transactional
    public DocumentResponse submitDocument(Long projectId, Long documentId) {

        Document document = getDocument(projectId, documentId);

        if (DocumentStatus.DRAFT != document.getStatus()) {
            throw new InvalidDocumentStateException("Only DRAFT document can be submitted");
        }

        document.setStatus(DocumentStatus.SUBMITTED);

        //Document savedDocument = documentRepository.save(document);

        //inject auditserivce for audit record
        documentAuditService.record(
                document,
                AuditAction.SUBMITTED,
                "Document submitted"
        );

//        //direct publish to kafka
//        documentEventProducer.publisDocumentSubmitted(
//                projectId,
//                documentId
//        );

        // Cara baru: simpan event dalam outbox table
        outboxService.saveDocumentSubmitted(projectId, documentId);

        //check if revision exsists
        DocumentRevision currentRevision =
                documentRevisionRepository
                        .findByDocumentIdOrderByRevisionNoDesc(documentId)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Document has no revision"
                                ));

        return mapToResponse(document, currentRevision);
    }

//    @Transactional
//    public DocumentResponse approveDocument(Long projectId, Long documentId) {
//
//        Document document = getSubmittedDocument(projectId, documentId);
//
//        document.setStatus(DocumentStatus.APPROVED);
//
//        DocumentRevision currentRevision = getLatestRevision(documentId);
//
//        return mapToResponse(document, currentRevision);
//    }

    //pessismistic locking
    @Transactional
    public DocumentResponse approveDocument(Long projectId, Long documentId) {

        //1. Retrieve document and acquire database lock
        Document document = documentRepository.findByIdAndProjectIdForUpdate(documentId, projectId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        //2. Validate current document status
        if (document.getStatus() != DocumentStatus.SUBMITTED) {
            throw new InvalidDocumentStateException("Only SUBMITTED documents can be approved.");
        }

        //3. Update Status
        document.setStatus(DocumentStatus.APPROVED);

        //4. FLush update to database
        //saveAndFlush() memaksa Hibernate menghantar pending SQL update ke database sebelum method selesai.
        //Tetapi flush bukan commit.
        documentRepository.saveAndFlush(document);

        //inject auditserivce for audit record
        documentAuditService.record(
                document,
                AuditAction.APPROVED,
                "Document approved"
        );

        //5. Retrieve lates revision
        DocumentRevision latestRevision = getLatestRevision(document.getId());

        //6. return response
        return mapToResponse(document, latestRevision);
    }

    @Transactional
    public DocumentResponse requestRevision(Long projectId,Long documentId) {

        Document document = getSubmittedDocument(projectId, documentId);

        document.setStatus(DocumentStatus.REVISION_REQUIRED);

        //inject auditserivce for audit record
        documentAuditService.record(
                document,
                AuditAction.REVISION_REQUESTED,
                "Document revision requested"
        );

        DocumentRevision currentRevision = getLatestRevision(documentId);

        return mapToResponse(document, currentRevision);
    }

    @Transactional
    public DocumentResponse rejectDocument(Long projectId, Long documentId) {

        Document document = getSubmittedDocument(projectId, documentId);

        document.setStatus(DocumentStatus.REJECTED);

        //inject auditserivce for audit record
        documentAuditService.record(
                document,
                AuditAction.REJECTED,
                "Document rejected"
        );

        DocumentRevision currentRevision = getLatestRevision(documentId);

        return mapToResponse(document, currentRevision);
    }

    @Transactional
    public DocumentResponse createRevision(Long projectId, Long documentId,
                                           DocumentRevisionRequest request) {

        //1. check if document exists
        Document document = getDocument(projectId, documentId);

        //2. check document status
        if (document.getStatus() != DocumentStatus.REVISION_REQUIRED) {
            throw new InvalidDocumentStateException(
                    "New revision can only be created when revision is required");
        }

        //3. find lates version
        DocumentRevision latestRevision = getLatestRevision(documentId);

        //4. calculate next revision number
        int nextRevisionNo = latestRevision.getRevisionNo() + 1;

        //5. Create new revision
        DocumentRevision newRevision =  new DocumentRevision();

        newRevision.setRevisionNo(nextRevisionNo);
        newRevision.setFileName(request.getFileName());
        newRevision.setFilePath(request.getFilePath());
        newRevision.setRemarks(request.getRemarks());
        newRevision.setCreatedAt(LocalDateTime.now());
        newRevision.setDocument(document);

        DocumentRevision savedRevision = documentRevisionRepository.save(newRevision);

        //6. New revision becomes draft
        document.setStatus(DocumentStatus.DRAFT);

        return mapToResponse(document, savedRevision);
    }

    @Transactional(readOnly = true)
    public List<DocumentRevisionResponse> getRevisionHistory(Long projectId, Long documentId) {

        //make sure document exists
        getDocument(projectId, documentId);

        return documentRevisionRepository.findByDocumentIdOrderByRevisionNoAsc(documentId)
                .stream().map(this::mapRevisionToResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long projectId, Long documentId) {

        Document document = getDocument(projectId, documentId);

        DocumentRevision latestRevision =  getLatestRevision(documentId);

        return mapToResponse(document, latestRevision);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> getDocumentByProject(Long projectId, Pageable pageable) {

        //make sure project exists
        projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException(projectId));

        Page<Document> documents = documentRepository.findByProjectId(projectId, pageable);

        return documents.map(document -> {

            DocumentRevision latestRevision = getLatestRevision(document.getId());

            return mapToResponse(document, latestRevision);
        });
    }

    //this method contain pagination, sorting, status filtering dan N+1 query optimization.
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getDocumentsByProject(Long projectId, DocumentStatus status,
                                                        Pageable pageable) {

        //1.validate project exists
        projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException(projectId));

        //2. Retrieve documents with pagination and optional status filter
        Page<Document> documents;

        if (status != null) {
            documents = documentRepository.findByProjectIdAndStatus(projectId, status, pageable);
        } else {
            documents = documentRepository.findByProjectId(projectId, pageable);
        }

        //3. Return empty page if no documents found
        if (documents.isEmpty()) {
            return documents.map(document -> {
                throw new IllegalStateException("No documents to map");
            });
        }

        //4. Collect document Ids from current page
        List<Long> documentIds = documents.getContent().stream().map(Document::getId).toList();

        //5. Fetch latest revisions for all documents in one query
        List<DocumentRevision> latestRevisions = documentRevisionRepository
                .findLatestRevisionsByDocumentIds(documentIds);

        //6. Convert revisions into Map<DocumentId, DocumentRevision>
        Map<Long, DocumentRevision> revisionMap = latestRevisions.stream().collect(
                Collectors.toMap(revisions -> revisions.getDocument().getId(),
                        Function.identity()));

        //7. Map document entities to DocumentResponse DTOs
        return documents.map(document -> {

            DocumentRevision latestRevision =  revisionMap.get(document.getId());

            if (latestRevision == null) {
                throw new IllegalStateException(
                        "Document has no revisions: " + document.getId()
                );
            }

            return mapToResponse(document, latestRevision);
        });
    }

    @Transactional
    public DocumentResponse updateDocument(Long projectId, Long documentId, DocumentUpdateRequest request) {

        Document document = getDocument(projectId, documentId);

        if (!document.getVersion().equals(request.getVersion())) {
            throw new InvalidDocumentStateException("Document has been modified by another user. Please refresh");
        }

        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new InvalidDocumentStateException("Only DRAFT documents can be edited");
        }

        document.setTitle(request.getTitle());

        documentRepository.saveAndFlush(document);

        //inject auditserivce for audit record
        documentAuditService.record(
                document,
                AuditAction.UPDATED,
                "Document updated"
        );

        DocumentRevision latestRevision = getLatestRevision(document.getId());

        return mapToResponse(document, latestRevision);
    }

    private DocumentRevision getLatestRevision(Long documentId) {

        return documentRevisionRepository.findFirstByDocumentIdOrderByRevisionNoDesc(documentId)
                .orElseThrow(() -> new IllegalStateException("Document has no revision"));
    }

    private Document getSubmittedDocument(Long projectId, Long documentId) {

        Document document = getDocument(projectId, documentId);

        if (document.getStatus() != DocumentStatus.SUBMITTED) {
            throw new InvalidDocumentStateException("Only SUBMITTED document can be reviewed");
        }

        return document;

    }

    private Document getDocument(Long projectId, Long documentId) {

        return documentRepository.findByIdAndProjectId(documentId, projectId).orElseThrow(
                () -> new DocumentNotFoundException(documentId));
    }


    private DocumentResponse mapToResponse(Document document, DocumentRevision revision) {

        return new DocumentResponse(
                document.getId(),
                document.getDocumentNo(),
                document.getTitle(),
                document.getDocumentType(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getProject().getId(),
                document.getProject().getProjectNo(),
                revision.getRevisionNo(),
                revision.getFileName(),
                document.getVersion()
        );
    }

    private DocumentRevisionResponse mapRevisionToResponse(DocumentRevision revision) {

        return new DocumentRevisionResponse(
                revision.getId(),
                revision.getRevisionNo(),
                revision.getFileName(),
                revision.getFilePath(),
                revision.getRemarks(),
                revision.getCreatedAt()
        );
    }
}

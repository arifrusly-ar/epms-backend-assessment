package com.example.application_management.document;

import com.example.application_management.document.entity.Document;
import com.example.application_management.document.enums.DocumentStatus;
import com.example.application_management.document.exception.InvalidDocumentStateException;
import com.example.application_management.document.repository.DocumentRepository;
import com.example.application_management.document.repository.DocumentRevisionRepository;
import com.example.application_management.document.service.DocumentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentRevisionRepository documentRevisionRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void shouldRejectApprovalWhenDocumentIsDraft() {

        // Arrange
        Long projectId = 1L;
        Long documentId = 10L;

        Document document = new Document();
        //document.setId(documentId);
        document.setStatus(DocumentStatus.DRAFT);

        when(documentRepository.findByIdAndProjectIdForUpdate(
                documentId, projectId
        )).thenReturn(Optional.of(document));

        // Act + Assert
        assertThrows(
                InvalidDocumentStateException.class,
                () -> documentService.approveDocument(
                        projectId, documentId
                )
        );

        // Verify document was not updated
        verify(documentRepository, never())
                .saveAndFlush(any(Document.class));
    }
}
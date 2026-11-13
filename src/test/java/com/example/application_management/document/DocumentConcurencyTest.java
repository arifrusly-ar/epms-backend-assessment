package com.example.application_management.document;

import com.example.application_management.document.dto.DocumentRequest;
import com.example.application_management.document.dto.DocumentResponse;
import com.example.application_management.document.enums.DocumentStatus;
import com.example.application_management.document.exception.InvalidDocumentStateException;
import com.example.application_management.document.service.DocumentService;
import com.example.application_management.project.dto.ProjectRequest;
import com.example.application_management.project.dto.ProjectResponse;
import com.example.application_management.project.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocumentConcurrencyTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DocumentService documentService;

    @Test
    void shouldAllowOnlyOneConcurrentApproval() throws Exception {

        // 1. Create test project
        ProjectRequest projectRequest = new ProjectRequest();

        projectRequest.setProjectNo(
                "PRJ-TEST-" + UUID.randomUUID()
        );
        projectRequest.setProjectName("Concurrency Test Project");
        projectRequest.setClientName("ABC Development");
        projectRequest.setLocation("Kuala Lumpur");
        projectRequest.setStartDate(LocalDate.now());
        projectRequest.setEndDate(LocalDate.now().plusMonths(6));

        ProjectResponse project =
                projectService.createProject(projectRequest);

        Long projectId = project.getId();

        // 2. Create test document
        DocumentRequest documentRequest = new DocumentRequest();

        documentRequest.setDocumentNo("DOC-001");
        documentRequest.setTitle("Foundation Drawing");
        documentRequest.setDocumentType("DRAWING");
        documentRequest.setFileName("foundation-r0.pdf");

        DocumentResponse document =
                documentService.createDocument(
                        projectId,
                        documentRequest
                );

        Long documentId = document.getId();

        // 3. Change status from DRAFT to SUBMITTED
        documentService.submitDocument(projectId, documentId);

        // 4. Create two worker threads
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        // Both threads wait until we release them
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {

            // 5. Define approval task
            Callable<Boolean> approvalTask = () -> {

                ready.countDown();

                // Wait until both threads are ready
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException(
                            "Timed out waiting to start"
                    );
                }

                try {
                    documentService.approveDocument(
                            projectId,
                            documentId
                    );

                    return true;

                } catch (InvalidDocumentStateException ex) {

                    // Document already approved
                    return false;

                } catch (OptimisticLockingFailureException ex) {

                    // Another transaction modified the document
                    return false;
                }
            };

            // 6. Submit two concurrent tasks
            Future<Boolean> resultA =
                    executor.submit(approvalTask);

            Future<Boolean> resultB =
                    executor.submit(approvalTask);

            // 7. Wait until both threads are ready
            assertTrue(
                    ready.await(10, TimeUnit.SECONDS),
                    "Threads did not become ready"
            );

            // Release both threads at approximately same time
            start.countDown();

            // 8. Collect results
            boolean successA =
                    resultA.get(15, TimeUnit.SECONDS);

            boolean successB =
                    resultB.get(15, TimeUnit.SECONDS);

            System.out.println("Thread A → " +
                    (successA ? "Success" : "Conflict"));

            System.out.println("Thread B → " +
                    (successB ? "Success" : "Conflict"));

            // 9. Count successful approvals
            int successCount =
                    (successA ? 1 : 0)
                            + (successB ? 1 : 0);

            System.out.println("Success count = " + successCount);

            assertEquals(
                    1,
                    successCount,
                    "Exactly one approval should succeed"
            );

            // 10. Verify final document status
            DocumentResponse finalDocument =
                    documentService.getDocumentById(
                            projectId,
                            documentId
                    );

            System.out.println("Final document status = "
                    + finalDocument.getStatus());

            assertEquals(
                    DocumentStatus.APPROVED,
                    finalDocument.getStatus()
            );

        } finally {

            start.countDown();
            executor.shutdownNow();
        }
    }
}
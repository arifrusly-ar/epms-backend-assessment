package com.example.application_management.kafka.consumer;

import com.example.application_management.kafka.event.DocumentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class ReportingConsumer {

    private final ObjectMapper objectMapper;

    public ReportingConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "document-events",
            groupId = "reporting-group"
    )
    public void consume(String message) {

        DocumentEvent event =
                objectMapper.readValue(message, DocumentEvent.class);

        if ("DOCUMENT_SUBMITTED".equals(event.eventType())) {

            System.out.println("=== REPORTING SERVICE ===");
            System.out.println("Update Document Submission Report");
            System.out.println("Document ID: " + event.documentId());
            System.out.println("Project ID: " + event.projectId());
            System.out.println("=========================");
        }
    }
}
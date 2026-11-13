package com.example.application_management.kafka.outbox;

import com.example.application_management.kafka.event.DocumentEvent;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class OutboxService {

    // Repository untuk menyimpan outbox event
    private final OutboxEventRepository repository;

    // ObjectMapper untuk convert Java object kepada JSON
    private final ObjectMapper objectMapper;

    // Constructor injection
    public OutboxService(OutboxEventRepository repository,
                         ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    // Simpan event dalam database transaction sedia ada
    public void saveDocumentSubmitted(Long projectId, Long documentId) {

        // Cipta DocumentEvent dengan eventId unik
        DocumentEvent event = DocumentEvent.submitted(projectId, documentId);

        // Convert DocumentEvent kepada JSON String
        String json = objectMapper.writeValueAsString(event);

        // Bina outbox record untuk Kafka topic document-events
        OutboxEvent outboxEvent = new OutboxEvent(
                "document-events",
                documentId.toString(),
                json
        );

        // Simpan record ke database
        // Jika dipanggil dalam @Transactional Document Service,
        // insert ini menggunakan transaction yang sama
        repository.save(outboxEvent);
    }
}
package com.example.application_management.kafka.event;

import java.time.Instant;
import java.util.UUID;

//gunakan Java record kerana event ini hanya membawa data dan tidak memerlukan setter.
public record DocumentEvent(
        String eventId,
        String eventType,
        Long projectId,
        Long documentId,
        Instant timestamp
) {

    public static DocumentEvent submitted(Long projectId, Long documentId) {

        return new DocumentEvent(
                UUID.randomUUID().toString(),
                "DOCUMENT_SUBMITTED",
                projectId,
                documentId,
                Instant.now()
        );
    }
}

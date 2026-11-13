package com.example.application_management.kafka.consumer;

import com.example.application_management.kafka.event.DocumentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class DocumentEventConsumer {

    private final ObjectMapper objectMapper;

    public DocumentEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "document-events",
            groupId = "epms-document-group"
    )

    public void consume(String message) throws  Exception{

        System.out.println("Consumer received: " + message);

        DocumentEvent event = objectMapper.readValue(message, DocumentEvent.class);

        System.out.println("Event ID: " + event.eventId());
        System.out.println("Event Type: " + event.eventType());
        System.out.println("Project ID: " + event.projectId());
        System.out.println("Document ID: " + event.documentId());
        System.out.println("Timestamp: " + event.timestamp());
        //System.out.println("Kafka Consumer Received: " + message);
    }
}

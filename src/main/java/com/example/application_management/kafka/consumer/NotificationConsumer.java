package com.example.application_management.kafka.consumer;

import com.example.application_management.kafka.event.DocumentEvent;
import com.example.application_management.notification.service.NotificationProcessingService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationProcessingService processingService;

    public NotificationConsumer(
            ObjectMapper objectMapper,
            NotificationProcessingService processingService
    ) {
        this.objectMapper = objectMapper;
        this.processingService = processingService;
    }

//    @KafkaListener(
//            topics = "document-events",
//            groupId = "notification-group",
//            concurrency = "3"
//    )

    public void consume(ConsumerRecord<String, String> record) {

        DocumentEvent event =
                objectMapper.readValue(
                        record.value(),
                        DocumentEvent.class
                );

        if (!"DOCUMENT_SUBMITTED".equals(event.eventType())) {
            return;
        }

        try {
            processingService.process(event);

            System.out.println(
                    "SUCCESS: Event processed " + event.eventId()
            );

        } catch (DataIntegrityViolationException ex) {

            System.out.println(
                    "DUPLICATE EVENT SKIPPED: " + event.eventId()
            );
        }
    }
}
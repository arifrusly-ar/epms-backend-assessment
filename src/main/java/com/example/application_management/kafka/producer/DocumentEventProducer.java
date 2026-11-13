package com.example.application_management.kafka.producer;

import com.example.application_management.kafka.event.DocumentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class DocumentEventProducer {

    //private static final String TOPIC = "document-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DocumentEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publisDocumentSubmitted(Long projectId, Long documentId){

        try {

            DocumentEvent event = DocumentEvent.submitted(projectId, documentId);

            String json = objectMapper.writeValueAsString(event);

            //TOPIC — destinasi message, iaitu document-events.
            //documentId.toString() — message key. Kita gunakan document ID supaya events bagi document yang sama boleh dihantar ke partition yang sama.
            //message — data yang dihantar.
            kafkaTemplate.send(
                    "document-events",
                    documentId.toString(),
                    json
            );

            System.out.println("Kafka event send requested: " + json);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to serialize document event", e
            );
        }

    }
}

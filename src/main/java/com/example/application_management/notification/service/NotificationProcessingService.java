package com.example.application_management.notification.service;

import com.example.application_management.kafka.entity.ProcessedKafkaEvent;
import com.example.application_management.kafka.event.DocumentEvent;
import com.example.application_management.kafka.repository.ProcessedKafkaEventRepository;
import com.example.application_management.notification.entity.Notification;
import com.example.application_management.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Menandakan class ini sebagai Spring Service
@Service
public class NotificationProcessingService {

    // Repository untuk menyimpan rekod Kafka event yang telah diproses
    private final ProcessedKafkaEventRepository processedRepository;

    // Constructor untuk dependency injection
    public NotificationProcessingService(
            ProcessedKafkaEventRepository processedRepository) {

        // Menyimpan dependency yang diberikan oleh Spring
        this.processedRepository = processedRepository;
    }

    // Memastikan semua operasi database berada dalam satu transaction
    @Transactional
    public void process(DocumentEvent event) {

        // Mencipta rekod untuk menandakan event ini telah diproses
        ProcessedKafkaEvent processedEvent =
                new ProcessedKafkaEvent(
                        event.eventId(),       // ID unik Kafka event
                        "notification-group"   // Consumer group yang memproses event
                );

        // Menyimpan rekod ke database dan menjalankan SQL INSERT segera
        processedRepository.saveAndFlush(processedEvent);
    }
}

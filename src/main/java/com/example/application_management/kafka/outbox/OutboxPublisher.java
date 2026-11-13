package com.example.application_management.kafka.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxPublisher {

    // Repository untuk membaca dan update outbox record
    private final OutboxEventRepository repository;

    // KafkaTemplate untuk publish message ke Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Constructor injection
    public OutboxPublisher(OutboxEventRepository repository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Scheduler berjalan setiap 5 saat selepas run sebelumnya selesai
    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        // Cari semua event yang belum dihantar
        var events = repository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING
        );

        // Proses setiap pending event
        for (OutboxEvent event : events) {

            try {

                // Hantar event ke Kafka dan tunggu broker acknowledgement
                // get() menjadikan operasi asynchronous ini ditunggu
                kafkaTemplate.send(
                        event.getTopic(),
                        event.getMessageKey(),
                        event.getPayload()
                ).get();

                // Hanya mark SENT selepas Kafka mengesahkan penghantaran
                event.markSent();

                // Simpan status SENT ke database
                repository.save(event);

                System.out.println(
                        "OUTBOX SENT: " + event.getId()
                );

            } catch (Exception ex) {

                // Jika Kafka down, event kekal PENDING
                // Scheduler akan mencuba semula pada run berikutnya
                System.err.println(
                        "OUTBOX FAILED: " + event.getId()
                                + " | " + ex.getMessage()
                );
            }
        }
    }
}
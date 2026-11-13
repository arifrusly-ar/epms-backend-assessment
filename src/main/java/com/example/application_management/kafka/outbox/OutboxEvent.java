package com.example.application_management.kafka.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

// Menandakan class ini sebagai JPA Entity
@Entity

// Menentukan nama table dalam database
@Table(name = "outbox_events")
public class OutboxEvent {

    // Primary key bagi setiap outbox record
    @Id
    private String id;

    // Nama Kafka topic yang akan menerima event
    @Column(nullable = false)
    private String topic;

    // Kafka message key, contohnya documentId
    @Column(name = "message_key", nullable = false)
    private String messageKey;

    // JSON event yang akan dihantar ke Kafka
    @Lob
    @Column(nullable = false)
    private String payload;

    // Status penghantaran: PENDING atau SENT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    // Masa event direkodkan dalam outbox
    @Column(nullable = false)
    private Instant createdAt;

    // Masa Kafka mengesahkan penghantaran event
    private Instant sentAt;

    // Diperlukan oleh JPA untuk membina entity
    protected OutboxEvent() {
    }

    // Constructor untuk mencipta outbox event baharu
    public OutboxEvent(
            String topic,
            String messageKey,
            String payload
    ) {
        // Generate ID unik untuk outbox record
        this.id = UUID.randomUUID().toString();

        // Simpan nama Kafka topic
        this.topic = topic;

        // Simpan Kafka message key
        this.messageKey = messageKey;

        // Simpan JSON payload
        this.payload = payload;

        // Event belum dihantar ke Kafka
        this.status = OutboxStatus.PENDING;

        // Rekod masa event dicipta
        this.createdAt = Instant.now();
    }

    // Dipanggil selepas Kafka mengesahkan penghantaran
    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = Instant.now();
    }

    // Getter untuk membaca data entity
    public String getId() { return id; }
    public String getTopic() { return topic; }
    public String getMessageKey() { return messageKey; }
    public String getPayload() { return payload; }
    public OutboxStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}
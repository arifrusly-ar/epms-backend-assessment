package com.example.application_management.kafka.outbox;

// Senarai status yang dibenarkan untuk outbox event
public enum OutboxStatus {

    // Event telah disimpan tetapi belum berjaya dihantar
    PENDING,

    // Kafka telah mengesahkan penghantaran event
    SENT
}
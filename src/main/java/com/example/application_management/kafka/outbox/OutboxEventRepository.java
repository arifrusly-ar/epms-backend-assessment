package com.example.application_management.kafka.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, String> {

    // Cari event yang masih PENDING
    // Order by createdAt untuk menghantar event lama dahulu
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
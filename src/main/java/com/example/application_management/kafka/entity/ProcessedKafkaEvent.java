package com.example.application_management.kafka.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "processed_kafka_events",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"consumer_group", "event_id"}
        )
)
public class ProcessedKafkaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "consumer_group", nullable = false)
    private String consumerGroup;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedKafkaEvent() {
    }

    public ProcessedKafkaEvent(
            String eventId,
            String consumerGroup
    ) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
        this.processedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
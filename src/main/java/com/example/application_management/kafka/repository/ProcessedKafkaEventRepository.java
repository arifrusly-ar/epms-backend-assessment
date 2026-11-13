package com.example.application_management.kafka.repository;

import com.example.application_management.kafka.entity.ProcessedKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedKafkaEventRepository extends JpaRepository<ProcessedKafkaEvent, Long> {

}

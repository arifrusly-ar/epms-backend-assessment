package com.example.application_management.notification.repository;

import com.example.application_management.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {
}
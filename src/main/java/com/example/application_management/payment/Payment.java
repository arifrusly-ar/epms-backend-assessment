//package com.example.application_management.payment;
//
//
//import jakarta.persistence.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(
//        name = "payments",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_payment_idempotency",
//                        columnNames = "idempotency_key"
//                )
//        }
//)
//public class Payment {
//
//    // Primary key untuk setiap payment
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Reference kepada application EPMS
//    @Column(nullable = false)
//    private Long applicationId;
//
//    // Amount menggunakan BigDecimal untuk ketepatan
//    @Column(nullable = false, precision = 19, scale = 2)
//    private BigDecimal amount;
//
//    // Status payment disimpan sebagai String
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentStatus status;
//
//    // Unique key untuk elakkan duplicate payment request
//    @Column(
//            name = "idempotency_key",
//            nullable = false,
//            unique = true
//    )
//    private String idempotencyKey;
//
//    // Masa payment dibuat
//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//
//    // Optimistic locking untuk concurrent update
//    @Version
//    private Long version;
//
//    @PrePersist
//    public void onCreate() {
//        // Set masa creation secara automatik
//        createdAt = LocalDateTime.now();
//    }
//
//
//
//    public Long getId() {
//        return id;
//    }
//
//    public Long getApplicationId() {
//        return applicationId;
//    }
//
//    public void setApplicationId(Long applicationId) {
//        this.applicationId = applicationId;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public PaymentStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(PaymentStatus status) {
//        this.status = status;
//    }
//
//    public String getIdempotencyKey() {
//        return idempotencyKey;
//    }
//
//    public void setIdempotencyKey(String idempotencyKey) {
//        this.idempotencyKey = idempotencyKey;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Long getVersion() {
//        return version;
//    }
//
//    public void setVersion(Long version) {
//        this.version = version;
//    }
//}

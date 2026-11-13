//package com.example.application_management.payment;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.Optional;
//
//public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
//    // Cari payment berdasarkan idempotency key
//    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
//}

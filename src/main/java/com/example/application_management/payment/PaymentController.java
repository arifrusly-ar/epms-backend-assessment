//package com.example.application_management.payment;
//
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/payments")
//public class PaymentController {
//
//    // Inject PaymentService
//    private final PaymentService paymentService;
//
//    public PaymentController(
//            PaymentService paymentService) {
//
//        this.paymentService = paymentService;
//    }
//
//    // API 1: Create payment
//    @PostMapping
//    public ResponseEntity<PaymentResponse> createPayment(
//            @Valid @RequestBody PaymentRequest request,
//            // Client wajib menghantar Idempotency-Key
//            @RequestHeader("Idempotency-Key") String idempotencyKey) {
//
//        // Call service untuk create payment
//        PaymentResponse response =
//                paymentService.createPayment(request);
//
//        // Return HTTP 201 Created
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(response);
//    }
//
//    // API 2: Check payment status
//    @GetMapping("/{id}")
//    public ResponseEntity<PaymentResponse> getPayment(
//            @PathVariable Long id) {
//
//        // Cari payment berdasarkan ID
//        PaymentResponse response =
//                paymentService.getPayment(id);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // API 3: Cancel payment
//    @PostMapping("/{id}/cancel")
//    public ResponseEntity<PaymentResponse> cancelPayment(
//            @PathVariable Long id) {
//
//        // Call service untuk cancel payment
//        PaymentResponse response =
//                paymentService.cancelPayment(id);
//
//        return ResponseEntity.ok(response);
//    }
//}

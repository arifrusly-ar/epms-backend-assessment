//package com.example.application_management.payment;
//
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.TransactionTemplate;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Objects;
//import java.util.UUID;
//
//@Service
//public class PaymentService {
//
//    private final PaymentRepository paymentRepository;
//    // TransactionTemplate membolehkan kita menangani
//// database conflict selepas transaction selesai/rollback
//    private final TransactionTemplate transactionTemplate;
//
//    public PaymentService(PaymentRepository paymentRepository,
//                          TransactionTemplate transactionTemplate) {
//        this.paymentRepository = paymentRepository;
//        this.transactionTemplate = transactionTemplate;
//    }
//
//    // Create payment dengan idempotency protection
//// Jangan letak @Transactional di method ini
//    public PaymentResponse createPayment(PaymentRequest request,
//                                         String idempotencyKey) {
//
//        //Reject empty atau terlalu panjang key
//        if (idempotencyKey == null || idempotencyKey.isBlank()
//        || idempotencyKey.length() > 200) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Invalid Idempotency-Key"
//            );
//        }
//
//        //Buang leading/trailing whitespace
//        String key = idempotencyKey.trim();
//
//        //Check sama ada payment sudah wujud
//        Payment existing = paymentRepository.findByIdempotencyKey(key)
//                .orElse(null);
//
//        if (existing != null) {
//            // Validate request payload
//            validateSameRequest(existing, request);
//
//            // Return payment asal
//            return mapToResponse(existing);
//        }
//
//
//        try {
//
//            //Cipta entity Payment baru
//            Payment payment = new Payment();
//
//            //Set application ID daripada request
//            payment.setApplicationId(request.applicationId());
//
//            //Set amount daripada request
//            payment.setAmount(request.amount());
//
//            //Semua paymnet baharu bermula dengan PENDING
//            payment.setStatus(PaymentStatus.PENDING);
//
//            //Temporary unique key untuk step 2
//            //step 3 akan menerima kay daripada HTTP header
//            payment.setIdempotencyKey(key);
//
//            // saveAndFlush memaksa SQL INSERT
//            // supaya unique constraint diuji sekarang
//            Payment savedPayment = paymentRepository.saveAndFlush(payment);
//
//            //Return payment response
//            return mapToResponse(savedPayment);
//
//        } catch(DataIntegrityViolationException ex) {
//            // Kemungkinan request serentak menggunakan key sama.
//            // Transaction insert telah rollback sebelum sampai sini.
//
//            Payment savedPayment = paymentRepository
//                    .findByIdempotencyKey(key)
//                    .orElse(null);
//
//            if (savedPayment == null) {
//
//                // Mungkin database error bukan disebabkan
//                // duplicate idempotency key
//                throw ex;
//            }
//
//            // Pastikan payload request sama
//            validateSameRequest(savedPayment, request);
//
//            // Return existing payment
//            return mapToResponse(savedPayment);
//
//        }
//    }
//
//    private void validateSameRequest(Payment existing, PaymentRequest request) {
//
//        //Compare application id
//        boolean sameApplication = Objects.equals(
//                existing.getApplicationId(),
//                request.applicationId()
//        );
//
//        //compare amount menggunakan compareTo
//        boolean sameAmount = existing.getAmount().compareTo(request.amount()) == 0;
//
//        //jika salah satu berbeza , reject request
//        if (!sameApplication || !sameAmount) {
//            throw new PaymentConflictException(
//                    "Idempotency key already used " +
//                            "with different payment details"
//            );
//        }
//    }
//
//    //Step 2: Get Payment by id
//    @Transactional(readOnly = true)
//    public PaymentResponse getPayment(Long id) {
//
//        //Cari payment dalam database
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() ->
//                        new ResponseStatusException(
//                                HttpStatus.NOT_FOUND,
//                                "Payment not found"
//                        )
//                );
//
//        return mapToResponse(payment);
//    }
//
//    //step 3: Cancel Payment
//    @Transactional
//    public PaymentResponse cancelPayment(Long id) {
//
//        // Cari payment berdasarkan ID
//        Payment payment = paymentRepository
//                .findById(id)
//                .orElseThrow(() ->
//                        new ResponseStatusException(
//                                HttpStatus.NOT_FOUND,
//                                "Payment not found"
//                        )
//                );
//
//        //Hanya pending payment boleh dibatalkan
//        if(payment.getStatus() != PaymentStatus.PENDING) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT,
//                    "Only PENDING payments can be cancelled"
//            );
//        }
//
//        //update status payment kepada cancelled
//        payment.setStatus(PaymentStatus.CANCELLED);
//
//        // Save payment
//        Payment updatedPayment =
//                paymentRepository.save(payment);
//
//        return mapToResponse(updatedPayment);
//    }
//
//
//    // Convert Payment entity kepada PaymentResponse DTO
//    private PaymentResponse mapToResponse(Payment payment) {
//
//        return new PaymentResponse(
//                payment.getId(),
//                payment.getApplicationId(),
//                payment.getAmount(),
//                payment.getStatus(),
//                payment.getCreatedAt()
//        );
//    }
//}

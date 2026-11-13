//package com.example.application_management.payment;
//
//import jakarta.validation.constraints.DecimalMin;
//import jakarta.validation.constraints.Digits;
//import jakarta.validation.constraints.NotNull;
//
//import java.math.BigDecimal;
//
//public record PaymentRequest (
//
//        // Application EPMS yang hendak membuat payment
//        @NotNull(message = "Application ID is required")
//        Long applicationId,
//
//        // Payment amount mesti lebih daripada 0
//        @NotNull(message = "Amount is required")
//        @DecimalMin(
//                value = "0.00",
//                inclusive = false,
//                message = "Amount must be greater than zero"
//        )
//        @Digits(integer = 17, fraction = 2)
//        BigDecimal amount
//){
//}

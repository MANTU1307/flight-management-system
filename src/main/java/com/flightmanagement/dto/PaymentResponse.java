package com.flightmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
}

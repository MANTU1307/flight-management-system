package com.flightmanagement.model;

import com.flightmanagement.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // one booking -> one payment
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Razorpay Order ID
    @Column(nullable = false)
    private String razorpayOrderId;

    // Razorpay Payment ID — after payment
    private String razorpayPaymentId;

    // Razorpay Signature — for verification
    private String razorpaySignature;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

package com.flightmanagement.controller;

import com.flightmanagement.dto.ApiResponse;
import com.flightmanagement.dto.PaymentRequest;
import com.flightmanagement.dto.PaymentResponse;
import com.flightmanagement.dto.PaymentVerifyRequest;
import com.flightmanagement.service.PaymentService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Step 1 — Create Razorpay Order
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody PaymentRequest request) {

        PaymentResponse payment = paymentService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Payment order created successfully!", payment));
    }

    // Step 2 — Verify Payment
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) throws RazorpayException {
        PaymentResponse payment = paymentService.verifyPayment(request);
        return ResponseEntity.ok(new ApiResponse(true, "Payment verified successfully! " +
                "Booking confirmed!", payment));
    }

    // Get Payment By Booking ID
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse> getPaymentByBookingId(@PathVariable Long bookingId) {

        PaymentResponse payment = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(new ApiResponse(true, "Payment fetched successfully!", payment));
    }
}

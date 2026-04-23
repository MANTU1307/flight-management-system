package com.flightmanagement.repository;

import com.flightmanagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    //  Booking ka payment find karna
    Optional<Payment> findByBookingId(Long bookingId);

    // Razorpay Order ID se find karna
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}

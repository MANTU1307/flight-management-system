package com.flightmanagement.service;

import com.flightmanagement.dto.PaymentRequest;
import com.flightmanagement.dto.PaymentResponse;
import com.flightmanagement.dto.PaymentVerifyRequest;
import com.flightmanagement.exception.BookingNotFoundException;
import com.flightmanagement.exception.PaymentException;
import com.flightmanagement.model.Booking;
import com.flightmanagement.model.Payment;
import com.flightmanagement.model.enums.BookingStatus;
import com.flightmanagement.model.enums.PaymentStatus;
import com.flightmanagement.repository.BookingRepository;
import com.flightmanagement.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // Step 1 — Razorpay Order Create
    @Transactional
    public PaymentResponse createOrder(PaymentRequest request) {
        // Booking fetch karna
        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found!"));

        // Already paid?
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new PaymentException("Booking is already confirmed!");
        }
        // Cancelled booking pay nahi kar sakte
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new PaymentException("Cannot pay for cancelled booking!");
        }
        try {
            // Razorpay client
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            // Order options
            JSONObject options = new JSONObject();
            options.put("amount", (int) (booking.getTotalAmount() * 100));
            options.put("currency", "INR");
            options.put("receipt", "booking_" + booking.getId());
            Order order = razorpay.orders.create(options);

            // Payment record
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setAmount(booking.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());

            Payment saved = paymentRepository.save(payment);
            return mapToResponse(saved);

        } catch (RazorpayException e) {
            throw new PaymentException("Error creating payment order: " + e.getMessage());
        }
    }

    // Step 2 — Payment Verify
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request) throws RazorpayException {
        // Payment fetch
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentException("Payment not found!"));

        // HMAC SHA256 Signature Verify
        boolean isValid = verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature());

        if (isValid) {
            // Payment successful
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Booking CONFIRMED
            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            return mapToResponse(payment);

        } else {
            // Payment failed
            payment.setStatus(PaymentStatus.FAILED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            throw new PaymentException("Payment verification failed! " + "Invalid signature.");
        }
    }

    // HMAC SHA256 Signature Verify
    private boolean verifySignature(
            String orderId,
            String paymentId,
            String signature) {

        try {
            String data = orderId + "|" + paymentId;

            // HMAC SHA256 with secret key
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);

            // Hash generate karna
            byte[] hash = mac.doFinal(data.getBytes());
            // Hex string mein convert karna
            String generatedSignature = HexFormat.of().formatHex(hash);

            // Razorpay signature se compare karna
            return generatedSignature.equals(signature);

        } catch (Exception e) {
            throw new PaymentException("Signature verification error: " + e.getMessage());
        }
    }

    // Get Payment By Booking ID
    public PaymentResponse getPaymentByBookingId(Long bookingId) {

        Payment payment = paymentRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentException("Payment not found for booking: " + bookingId));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setBookingId(payment.getBooking().getId());
        response.setRazorpayOrderId(payment.getRazorpayOrderId());
        response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus().name());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}

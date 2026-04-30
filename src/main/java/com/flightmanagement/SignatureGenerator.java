package com.flightmanagement;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

public class SignatureGenerator {
    public static void main(String[] args) throws Exception {
        String orderId = "order_Sg3LXjwcErqA79";
        String paymentId = "pay_test_123456";
        String secret = System.getenv("RAZORPAY_SECRET");

        // Signature generate karo
        String data = orderId + "|" + paymentId;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(key);
        byte[] hash = mac.doFinal(data.getBytes());
        String signature = HexFormat.of().formatHex(hash);

        System.out.println("Order ID: " + orderId);
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Signature: " + signature);
    }
}

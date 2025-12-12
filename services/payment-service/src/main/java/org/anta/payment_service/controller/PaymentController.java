package org.anta.payment_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.payment_service.dto.response.CreateMomoResponse;
import org.anta.payment_service.dto.response.PaymentStatusResponse;
import org.anta.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private Logger logger = Logger.getLogger(PaymentController.class.getName());

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        // Lấy orderId (có thể null)
        Long orderId = null;
        Object orderIdObj = body.get("orderId");
        if (orderIdObj != null && !"null".equals(orderIdObj.toString())) {
            try {
                orderId = Long.valueOf(orderIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid orderId");
            }
        }

        // Lấy userId (có thể null)
        Long userId = null;
        Object userIdObj = body.get("userId");
        if (userIdObj != null && !"null".equals(userIdObj.toString())) {
            try {
                userId = Long.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid userId");
            }
        }

        // Lấy amount: chấp nhận "amount" hoặc "total"
        Object amountObj = body.get("amount");
        if (amountObj == null) amountObj = body.get("total"); // fallback
        if (amountObj == null) {
            return ResponseEntity.badRequest().body("Missing required field: amount (or total)");
        }
        Long amount;
        try {
            // amountObj có thể là Number hoặc String
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).longValue();
            } else {
                amount = Long.valueOf(amountObj.toString());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid amount");
        }

        CreateMomoResponse resp = paymentService.createPaymentAndRequestMomo(orderId, userId, amount, null);
        return ResponseEntity.ok(resp);
    }


    @GetMapping("/status/{requestId}")
    public ResponseEntity<PaymentStatusResponse> getStatus(@PathVariable String requestId) {
        PaymentStatusResponse resp = paymentService.checkMomoStatus(requestId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/check-status")
    public ResponseEntity<Map<String, String>> checkPaymentStatus(@RequestParam String orderId, @RequestParam String resultCode) {
        try {
            // Kiểm tra trạng thái thanh toán từ MoMo
            boolean isPaid = paymentService.checkPaymentStatus(orderId, resultCode);

            Map<String, String> response = new HashMap<>();
            if (isPaid) {
                response.put("status", "PAID");
            } else {
                response.put("status", "FAILED");
            }

            logger.info("PPPPPPPPPayment status for orderId " +
                    orderId + ": " + response.get("status"));

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "ERROR"));
        }
    }
}

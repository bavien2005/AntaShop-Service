package org.anta.payment_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.payment_service.dto.response.CreateMomoResponse;
import org.anta.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<CreateMomoResponse> create(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        Long userId = body.containsKey("userId") ? Long.valueOf(body.get("userId").toString()) : null;
        Long amount = Long.valueOf(body.get("amount").toString());

        CreateMomoResponse resp = paymentService.createPaymentAndRequestMomo(orderId, userId, amount, null);
        return ResponseEntity.ok(resp);
    }

}

package org.anta.payment_service.controller;

import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.anta.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/momo")
@RequiredArgsConstructor
@Slf4j
public class MomoController {

    private final PaymentService paymentService;

//    // MoMo IPN endpoint - MoMo sẽ POST form/query params or JSON
//    @PostMapping("/ipn-handler")
//    public ResponseEntity<String> handleIpn(@RequestBody(required = false) Map<String, String> jsonBody,
//                                            @RequestParam Map<String, String> allParams) {
//        // merge: if JSON provided use that, otherwise use form params
//        Map<String,String> params = (jsonBody != null && !jsonBody.isEmpty()) ? jsonBody : allParams;
//        log.info("[Momo IPN] Received params: {}", params);
//        boolean ok = paymentService.handleIpn(params);
//        if (ok) return ResponseEntity.ok("OK");
//        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID");
//    }
}

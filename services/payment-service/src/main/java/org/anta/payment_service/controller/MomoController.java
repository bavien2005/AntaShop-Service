package org.anta.payment_service.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anta.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/momo")
@Slf4j
public class MomoController {

    private final PaymentService paymentService;

    @RequestMapping(value = "/ipn-handler", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> handleIPN(@RequestParam Map<String, String> allParams) {
        log.info("IPN received: {}", allParams);

        boolean ok = paymentService.handleIpn(allParams);
        if (!ok) {
            log.warn("IPN processing failed for params: {}", allParams);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID");
        }
        return ResponseEntity.ok("OK");
    }
}

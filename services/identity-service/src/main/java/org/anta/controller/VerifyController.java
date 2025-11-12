package org.anta.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anta.client.MailClient;
import org.anta.dto.request.VerifyConfirmRequest;
import org.anta.dto.request.VerifyRequest;
import org.anta.service.OtpRedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/verify")
@RequiredArgsConstructor
public class VerifyController {

    private final OtpRedisService otpService;
    private final MailClient mailClient;

    @PostMapping("/request")
    public ResponseEntity<?> request(@Valid @RequestBody VerifyRequest req) {
        try {
            String otp = otpService.generateAndSave(req.getEmail());
            mailClient.sendResetCodeEmail(req.getEmail(), otp);
            return ResponseEntity.ok(Map.of(
                    "message", "OTP has been sent to your email.",
                    "requestId", UUID.randomUUID().toString()
            ));
        } catch (IllegalStateException cooldown) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", cooldown.getMessage()
            ));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@Valid @RequestBody VerifyConfirmRequest req) {
        boolean ok = otpService.verify(req.getEmail(), req.getOtp());
        if (!ok) {
            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "error", "OTP code is incorrect/expired or exceeds the number of attempts"
            ));
        }
        return ResponseEntity.ok(Map.of("verified", true));
    }
}

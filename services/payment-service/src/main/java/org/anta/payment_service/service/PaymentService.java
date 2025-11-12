package org.anta.payment_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.payment_service.dto.response.CreateMomoResponse;
import org.anta.payment_service.entity.Payment;
import org.anta.payment_service.entity.PaymentLog;
import org.anta.payment_service.repository.PaymentLogRepository;
import org.anta.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository logRepository;
    private final MomoService momoService;

    @Transactional
    public CreateMomoResponse createPaymentAndRequestMomo(Long orderId,
         Long userId, Long amount, Map<String,String> extra) {
        String requestId = java.util.UUID.randomUUID().toString();


        Payment p = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .requestId(requestId)
                .amount(amount)
                .currency("VND")
                .status("PENDING")
                .extraData(extra == null ? null : toJson(extra))
                .build();

        p = paymentRepository.save(p);

        CreateMomoResponse resp = momoService.createQRForPayment(p.getRequestId(),
                p.getAmount(), p.getOrderId().toString());

        p.setPartnerOrderId(resp.getOrderId());
        p.setPayUrl(resp.getPayUrl());
        paymentRepository.save(p);

        logRepository.save(PaymentLog.builder()
                .paymentId(p.getId())
                .eventType("MOMO_REQUEST_SENT")
                .payload(toJson(resp))
                .build());

        return resp;
    }

    @Transactional
    public boolean handleIpn(Map<String, String> ipnParams) {
        // verify signature
        if (!momoService.verifyIpnSignature(ipnParams)) {
            return false;
        }

        String partnerOrderId = ipnParams.getOrDefault("orderId", ipnParams.get("partnerOrderId"));

        String resultCode = ipnParams.get("resultCode");

        Optional<Payment> opt = paymentRepository.findByPartnerOrderId(partnerOrderId);
        if (opt.isEmpty()) {
            return false;
        }

        Payment p = opt.get();
        if ("SUCCESS".equalsIgnoreCase(p.getStatus())) {
            return true;
        }

        if (resultCode.equals("0")) {
            p.setStatus("SUCCESS");
        } else {
            p.setStatus("FAILED");
        }
        paymentRepository.save(p);

        logRepository.save(PaymentLog.builder()
                .paymentId(p.getId())
                .eventType("IPN_RECEIVED")
                .payload(toJson(ipnParams))
                .build());

        // notify order-service (via rest call or message broker)
        // e.g., call OrderService to mark order as PAID
        // orderClient.updatePaymentStatus(p.getOrderId(), p.getStatus());

        return true;
    }

    private String toJson(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) { return null; }
    }
}


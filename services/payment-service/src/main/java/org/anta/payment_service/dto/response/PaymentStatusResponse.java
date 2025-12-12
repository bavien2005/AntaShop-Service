package org.anta.payment_service.dto.response;

import org.anta.payment_service.entity.Payment;

public record PaymentStatusResponse(
        Long paymentId,
        Long orderId,
        String status,    // "PENDING", "SUCCESS", "FAILED"
        Integer resultCode,
        String message
) {
    public static PaymentStatusResponse from(Payment p) {
        return new PaymentStatusResponse(
                p.getId(),
                p.getOrderId(),
                p.getStatus(), // status là String
                null,
                null
        );
    }
}

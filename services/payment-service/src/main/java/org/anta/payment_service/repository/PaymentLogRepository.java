package org.anta.payment_service.repository;

import org.anta.payment_service.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentLogRepository extends JpaRepository<PaymentLog , Long> {

    List<PaymentLog> findByPaymentId(Long paymentId);
}

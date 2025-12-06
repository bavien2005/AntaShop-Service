package org.anta.order_service.client;

import org.anta.order_service.dto.external.CreatePaymentRequest;
import org.anta.order_service.dto.external.CreatePaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="payment", url="${payment.base-url}")
public interface PaymentClient {

    @PostMapping("/create")
    CreatePaymentResponse create(@RequestBody CreatePaymentRequest req);

}
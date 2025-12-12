package org.anta.payment_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@FeignClient(
        name = "orderClient",
        url = "${order.service.url}"
)
public interface OrderClient {

    // Đổi trạng thái đơn hàng sang PAID
    @PutMapping("/api/orders/{orderId}/paid")
    void markOrderPaid(@PathVariable("orderId") Long orderId);

    // Nếu bạn muốn handle fail:
    @PutMapping("/api/orders/{orderId}/payment-failed")
    void markOrderPaymentFailed(@PathVariable("orderId") Long orderId);

    @GetMapping("/api/orders/{orderId}")
    ResponseEntity<Map> getOrderById(@PathVariable("orderId") Long orderId);
}

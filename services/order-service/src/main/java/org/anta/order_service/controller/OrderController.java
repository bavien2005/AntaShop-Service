package org.anta.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.order_service.dto.request.CreateOrderRequest;
import org.anta.order_service.dto.response.CreateOrderResponse;
import org.anta.order_service.dto.response.OrderResponse;
import org.anta.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<CreateOrderResponse> create(@RequestBody CreateOrderRequest req){
        return ResponseEntity.ok(orderService.createOrder(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(orderService.get(id));
    }

    // Endpoint để payment-service gọi về sau IPN
    @PostMapping("/{id}/payment-status/{status}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @PathVariable String status){
        orderService.updatePaymentStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id){
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}

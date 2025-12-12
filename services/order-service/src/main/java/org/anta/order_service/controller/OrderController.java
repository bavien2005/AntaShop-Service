package org.anta.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.order_service.dto.request.CreateOrderRequest;
import org.anta.order_service.dto.request.ShippingRequest;
import org.anta.order_service.dto.response.CreateOrderResponse;
import org.anta.order_service.dto.response.OrderResponse;
import org.anta.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Logger log = Logger.getLogger(OrderController.class.getName());

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

    // --- trong cùng class OrderController ---
    @PostMapping("/{id}/payment-callback")
    public ResponseEntity<?> paymentCallback(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String status = body.get("status") != null ? body.get("status").toString() : null;
        Object paymentIdObj = body.get("paymentId");
        Object requestIdObj = body.get("requestId");

        log.info("Payment callback for orderId= " +id + "body={}"+ body);

        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "status required"));
        }

        // call existing service method
        orderService.updatePaymentStatus(id, status);

        // (Optional) you could record paymentId/requestId in order history if needed
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/paid")
    public ResponseEntity<Void> markPaid(@PathVariable Long id) {
        orderService.markAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/payment-failed")
    public ResponseEntity<Void> markPaymentFailed(@PathVariable Long id) {
        orderService.markPaymentFailed(id);
        return ResponseEntity.noContent().build();
    }
    // 1) List orders (GET /api/orders) — optional query params: search, status, orderNumber
    @GetMapping
    public ResponseEntity<List<OrderResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNumber) {

        List<OrderResponse> list = orderService.findOrders(search, status, orderNumber)
                .stream().map(order -> orderService.toResponse(order)).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // 2) Update generic order status (PUT /api/orders/{id}/status)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) return ResponseEntity.badRequest().body(Map.of("error", "status required"));
        orderService.updateStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Status updated"));
    }

    // 3) Arrange shipping (PUT /api/orders/{id}/shipping)
    @PutMapping("/{id}/shipping")
    public ResponseEntity<?> arrangeShipping(@PathVariable Long id, @RequestBody ShippingRequest req) {
        orderService.arrangeShipping(id, req);
        return ResponseEntity.ok(Map.of("message", "Shipping scheduled"));
    }
}

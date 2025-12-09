package org.anta.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.order_service.service.OrderRevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/revenue")
@RequiredArgsConstructor
public class OrderRevenueController {
    private final OrderRevenueService orderRevenueService;

    @GetMapping("/actual")
    public ResponseEntity<Double> getActualRevenue() {
        return ResponseEntity.ok(orderRevenueService.getActualRevenue());
    }
}
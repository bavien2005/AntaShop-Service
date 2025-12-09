package org.anta.cart_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.service.CartRevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart/revenue")
@RequiredArgsConstructor
public class CartRevenueController {
    private final CartRevenueService cartRevenueService;

    @GetMapping("/expected")
    public ResponseEntity<Double> getExpectedRevenue() {
        return ResponseEntity.ok(cartRevenueService.getExpectedRevenue());
    }
}
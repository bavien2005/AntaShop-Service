package org.anta.cart_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.response.TopProductDTO;
import org.anta.cart_service.service.DashboardCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class DashboardCartController {
    private final DashboardCartService dashboardService;

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDTO>> getTopProducts() {
        return ResponseEntity.ok(dashboardService.getTop10Products());
    }
}
// org/anta/cart_service/controller/CartRevenueController.java
package org.anta.cart_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.response.WeeklyRevenueDTO;
import org.anta.cart_service.service.CartRevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart/revenue")
@RequiredArgsConstructor
public class CartRevenueController {

    private final CartRevenueService cartRevenueService;

    // CHỈ DÙNG ENDPOINT NÀY CHO DASHBOARD
    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyRevenueDTO>> getExpectedRevenueWeekly() {
        return ResponseEntity.ok(cartRevenueService.getExpectedRevenueWeekly());
    }
}

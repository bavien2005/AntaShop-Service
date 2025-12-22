// org/anta/order_service/controller/OrderRevenueController.java
package org.anta.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.order_service.dto.response.ProductSoldQtyDTO;
import org.anta.order_service.dto.response.WeeklyRevenueDTO;
import org.anta.order_service.service.OrderRevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/revenue")
@RequiredArgsConstructor
public class OrderRevenueController {

    private final OrderRevenueService orderRevenueService;

    // CHỈ DÙNG ENDPOINT NÀY
    @GetMapping("/weekly")
    public ResponseEntity<List<WeeklyRevenueDTO>> getActualRevenueWeekly() {
        return ResponseEntity.ok(orderRevenueService.getActualRevenueWeekly());
    }

    @GetMapping("/products/sold-qty")
    public ResponseEntity<List<ProductSoldQtyDTO>> getSoldQtyByProduct() {
        return ResponseEntity.ok(orderRevenueService.getSoldQtyByProductFromPaidOrDelivered());
    }
}

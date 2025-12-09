package org.anta.services.revenue_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.services.revenue_service.dto.RevenueDTO;
import org.anta.services.revenue_service.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/revenue")
    public ResponseEntity<RevenueDTO> getRevenueComparison() {
        return ResponseEntity.ok(dashboardService.getRevenueComparison());
    }
}

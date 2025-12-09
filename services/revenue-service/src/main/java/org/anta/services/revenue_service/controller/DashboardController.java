// org/anta/services/revenue_service/controller/DashboardController.java
package org.anta.services.revenue_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.services.revenue_service.dto.WeeklyRevenueComparisonDTO;
import org.anta.services.revenue_service.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Endpoint cho FE vẽ biểu đồ
    @GetMapping("/revenue/weekly")
    public ResponseEntity<List<WeeklyRevenueComparisonDTO>> getWeeklyRevenueComparison() {
        return ResponseEntity.ok(dashboardService.getWeeklyRevenueComparison());
    }
}

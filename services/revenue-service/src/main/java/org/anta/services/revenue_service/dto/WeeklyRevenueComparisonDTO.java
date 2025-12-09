// org/anta/services/revenue_service/dto/WeeklyRevenueComparisonDTO.java
package org.anta.services.revenue_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyRevenueComparisonDTO {
    private String week;           // "2025-W01"
    private Double expectedRevenue;
    private Double actualRevenue;
}

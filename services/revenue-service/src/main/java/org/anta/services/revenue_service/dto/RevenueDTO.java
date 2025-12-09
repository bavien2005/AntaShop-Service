package org.anta.services.revenue_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueDTO {
    private Double expectedRevenue; // từ Cart Service
    private Double actualRevenue;   // từ Order Service
}

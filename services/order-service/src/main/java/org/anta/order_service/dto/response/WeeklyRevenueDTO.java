// org/anta/order_service/dto/response/WeeklyRevenueDTO.java
package org.anta.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyRevenueDTO {
    private String week;   // "2025-W01"
    private Double revenue;
}

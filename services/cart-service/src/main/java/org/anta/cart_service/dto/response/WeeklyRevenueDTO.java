// org/anta/cart_service/dto/response/WeeklyRevenueDTO.java
package org.anta.cart_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyRevenueDTO {
    private String week;   // ví dụ "2025-W01"
    private Double revenue;
}

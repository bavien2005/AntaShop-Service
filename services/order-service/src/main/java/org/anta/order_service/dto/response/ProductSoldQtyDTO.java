package org.anta.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSoldQtyDTO {
    private Long productId;
    private Long soldQty;
}
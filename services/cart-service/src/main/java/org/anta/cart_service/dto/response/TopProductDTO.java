package org.anta.cart_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductDTO {
    private Long productId;
    private String productName;
    private Long totalQuantity;
}
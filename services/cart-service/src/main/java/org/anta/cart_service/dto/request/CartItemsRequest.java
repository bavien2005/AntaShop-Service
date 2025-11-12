package org.anta.cart_service.dto.request;

import lombok.Data;
import org.anta.cart_service.entity.Carts;

@Data
public class CartItemsRequest {
    private Long userId;
    private String sessionId;
    private Long productId;
    private Long variantId;
    private String productName;
    private Double unitPrice;
    private Long quantity;
}

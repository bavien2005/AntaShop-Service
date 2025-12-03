package org.anta.cart_service.dto.request;

import lombok.Data;
import org.anta.cart_service.entity.Carts;

@Data
public class CartItemsRequest {
    private Long userId; // khach hang da login
    private String sessionId; // khach hang ch dang nhap
    private Long productId;
    private Long variantId;
    private String productName;
    private Double unitPrice;
    private Long quantity;
}

package org.anta.cart_service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CartItemsResponse {
    private Long id;

    private Long cartId;

    private Long productId;

    private Long variantId;

    private String productName;

    private Double unitPrice;

    private Long quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Double totalAmount;
}

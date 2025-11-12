package org.anta.cart_service.dto.response;
import org.anta.cart_service.enums.Status;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class CartsResponse {
    private Long id;

    private Long userId;

    private String sessionId;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    private List<CartItemsResponse> items;
}

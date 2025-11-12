package org.anta.cart_service.dto.request;
import org.anta.cart_service.enums.Status;

import lombok.Data;

@Data
public class CartsRequest {
    private Long userId;
    private String sessionId;
    private Status status;
}

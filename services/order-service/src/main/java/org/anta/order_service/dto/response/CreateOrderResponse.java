package org.anta.order_service.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponse {

    private Long orderId;
    private String status;
    private String payUrl;
    private String recipientName;
    private String recipientPhone;
    private String buyerName;
    private String email;
    private String orderNumber;
    private Long total;
}

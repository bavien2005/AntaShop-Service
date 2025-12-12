package org.anta.order_service.dto.request;


import java.util.List;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    private Long userId;

    private List<OrderItemRequest> items;

    private String shippingAddress;    // tùy bài toán

    private String paymentMethod;

    private String recipientName;
    private String recipientPhone;
    private String buyerName;
    private String email;
    private String orderNumber;
    private Long total;// "MOMO" ...
}

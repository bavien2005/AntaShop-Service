package org.anta.order_service.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    private Long productId;     // optional, để hiển thị

    private Long variantId;     // bắt buộc để giữ tồn

    private Integer quantity;

    private String note;        // optional

}
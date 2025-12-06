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

    private String payUrl;   // link thanh toán trả về cho FE

}

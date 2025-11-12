package com.example.orderservice.dto.request;

import com.example.orderservice.entity.Order;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class OrderItemRequest {
    String orderId;
    String productId;
    String productName;
    int quantity;
    BigDecimal priceAtPurchase;
    Order order;

}

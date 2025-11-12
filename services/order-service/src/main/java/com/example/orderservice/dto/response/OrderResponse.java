package com.example.orderservice.dto.response;

import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String orderId;
    String  userId;
    String orderDescription;
    String customerName;
    String customerPhone;
    String addressReceive;
    BigDecimal totalPrice;
    LocalDate createdDate;
    OrderStatus orderStatus = OrderStatus.PENDING;
    List<OrderItem> orderItems = new ArrayList<>();

//    int totalQuantity;
}

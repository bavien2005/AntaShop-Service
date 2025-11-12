package com.example.orderservice.dto.request;

import com.example.orderservice.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    String  userId;
    String orderDescription;
    String customerName;
    String customerPhone;
    String addressReceive;
    BigDecimal totalPrice;
    LocalDate createdDate;
    OrderStatus orderStatus = OrderStatus.PENDING;
    List<OrderItemRequest> orderItems = new ArrayList<>();
}

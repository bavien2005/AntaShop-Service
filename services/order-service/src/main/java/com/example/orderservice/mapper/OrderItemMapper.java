package com.example.orderservice.mapper;

import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toOrderItem(OrderItemRequest request);
}

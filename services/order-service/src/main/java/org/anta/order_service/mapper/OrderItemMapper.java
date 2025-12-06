package org.anta.order_service.mapper;

import org.anta.order_service.dto.request.OrderItemRequest;
import org.anta.order_service.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toOrderItem(OrderItemRequest request);
}

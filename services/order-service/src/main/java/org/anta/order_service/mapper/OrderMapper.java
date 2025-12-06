package org.anta.order_service.mapper;

import org.anta.order_service.entity.Order;
import org.anta.order_service.entity.OrderItem;
import org.anta.order_service.dto.response.OrderResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target="status", expression = "java(order.getStatus().name())")
    @Mapping(target="items", expression = "java(mapItems(order.getItems()))")
    OrderResponse toResponse(Order order);

    default List<OrderResponse.Item> mapItems(List<OrderItem> items){
        return items.stream().map(i -> OrderResponse.Item.builder()
                .productId(i.getProductId())
                .variantId(i.getVariantId())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .lineTotal(i.getLineTotal())
                .build()).collect(Collectors.toList());
    }
}

package org.anta.cart_service.mapper;

import org.anta.cart_service.dto.request.CartItemsRequest;
import org.anta.cart_service.dto.response.CartItemsResponse;
import org.anta.cart_service.entity.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemsMapper {

    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(expression = "java(entity.getUnitPrice() != null && entity.getQuantity() != null ? entity.getUnitPrice() * entity.getQuantity() : 0.0)", target = "totalAmount")
    CartItemsResponse toResponse(CartItems entity);

    CartItems toEntity(CartItemsRequest dto);
}
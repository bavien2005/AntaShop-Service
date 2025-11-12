package org.anta.cart_service.mapper;

import org.anta.cart_service.dto.request.CartItemsRequest;
import org.anta.cart_service.dto.response.CartItemsResponse;
import org.anta.cart_service.entity.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemsMapper {

    @Mapping(source = "cart.id", target = "cartId")
    CartItemsResponse toResponse(CartItems entity);

    CartItems toEntity(CartItemsRequest dto);
}
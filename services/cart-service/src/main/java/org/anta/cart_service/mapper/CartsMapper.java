package org.anta.cart_service.mapper;

import org.anta.cart_service.dto.response.CartsResponse;
import org.anta.cart_service.entity.Carts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemsMapper.class})
public interface CartsMapper {

    // Map từng trường rõ ràng
    @Mapping(source = "items", target = "items")
    CartsResponse toResponse(Carts carts);
}
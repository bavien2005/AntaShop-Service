package com.example.orderservice.mapper;

import com.example.orderservice.dto.request.OrderCreationRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "orderItems",  ignore = true)
    Order toOrder(OrderCreationRequest request);
    @Named("detail")
    OrderResponse toOrderResponse(Order order);
    List<OrderResponse> toOrderResponseList(List<Order> orders);


//    @Mapping(target = "totalQuantity", source = "order", qualifiedByName = "calculateTotalQuantity") // <-- Bổ sung mapping này
//    OrderResponse toResponse(Order order);
//    @IterableMapping(qualifiedByName = "detail")
//    List<OrderResponse> toOrderResponseList(List<Order> orders);
//
//    // Phương thức tính tổng số lượng (PHẢI LÀ default method hoặc class riêng)
//    @Named("calculateTotalQuantity")
//    default int calculateTotalQuantity(Order order) {
//        if (order.getOrderItems() == null) {
//            return 0;
//        }
//        return order.getOrderItems().stream()
//                .mapToInt(item -> item.getQuantity()) // Lấy trường quantity của mỗi Item
//                .sum(); // Tính tổng
//    }

}

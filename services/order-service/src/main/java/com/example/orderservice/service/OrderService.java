package com.example.orderservice.service;

import com.example.orderservice.dto.request.OrderCreationRequest;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.Product;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.mapper.OrderItemMapper;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderItemRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    ProductRepository productRepository;

    OrderMapper orderMapper;
    OrderItemMapper orderItemMapper;

    @Transactional
    public Order createNewOrder(OrderCreationRequest request){
        Order order = orderMapper.toOrder(request);

        BigDecimal totalPrice = BigDecimal.valueOf(0);

        for (OrderItemRequest itemReq : request.getOrderItems()){
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            // Tao item va co dinh price
            OrderItem orderItem = orderItemMapper.toOrderItem(itemReq);
            orderItem.setProductName(product.getProductName());
            orderItem.setPriceAtPurchase(product.getProductPrice());

            BigDecimal res = (orderItem.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity())) );
            totalPrice = totalPrice.add(res);

            order.setTotalPrice(totalPrice);
            order.setCreatedDate(LocalDate.now());
            //Add item vao Order
            order.addOrderItem(orderItem);

        }
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);


    }

    public List<OrderResponse> getAllOrders(String userId){
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(orderMapper::toOrderResponse)//.map(order -> orderMapper.toOrderResponse(order))
                .toList(); //Chuyen list order -> list orderRp

    }
    public OrderResponse getOrderById(String id){
        return orderMapper.toOrderResponse(orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    public OrderResponse cancelOrder(String id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if(order.getOrderStatus() == OrderStatus.PENDING){
            order.setOrderStatus(OrderStatus.CANCELED);
            Order cancelledOrder = orderRepository.save(order);
            return orderMapper.toOrderResponse(cancelledOrder);
        }else {
            throw new RuntimeException("Order status mustn't be CANCELED");
        }
    }




}

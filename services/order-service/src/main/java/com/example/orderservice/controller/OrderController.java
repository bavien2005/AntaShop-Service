package com.example.orderservice.controller;


import com.example.orderservice.dto.request.OrderCreationRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    Order createOrder(@RequestBody OrderCreationRequest request){
        return orderService.createNewOrder(request);
    }

    @GetMapping("/user/{userIds}")
    // Xem danh sach don hang da tao
    List<OrderResponse> getAllOrders(@PathVariable("userIds") String userId){
        return orderService.getAllOrders(userId);
    }
    //Xem chi tiet don hang
    @GetMapping("/order/{orderId}")
    OrderResponse order(@PathVariable("orderId") String orderId){
        return orderService.getOrderById(orderId);
    }
    // Huy don hang (Pennding)
    @DeleteMapping("/cancel/{orderIds}")
    OrderResponse cancelOrder(@PathVariable("orderIds") String orderIds){
        return orderService.cancelOrder(orderIds);
    }


}

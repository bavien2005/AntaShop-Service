package com.example.orderservice.enums;

public enum OrderStatus {
    PENDING,        // Khách hàng vừa đặt
    CONFIRMED,      // Admin/Shop xác nhận
    SHIPPED,        // Đang giao
    DELIVERED,      // Giao thành công
    CANCELED        // Đã hủy
}

package com.example.orderservice.entity;

import com.example.orderservice.enums.OrderStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table (name = "orders")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String  orderId;
    @Column (nullable = true)
    String  userId;
    String orderDescription;
    String customerName;
    String customerPhone;
    String addressReceive;
    BigDecimal totalPrice;
    LocalDate createdDate;
    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @lombok.Builder.Default // Tranh list bi null neu khong build
    List<OrderItem> orderItems = new ArrayList<>();

    //Dong bo 2 chieu
    public void addOrderItem(OrderItem item){
        orderItems.add(item);
        item.setOrder(this);
    }
//    int totalQuantity;

}

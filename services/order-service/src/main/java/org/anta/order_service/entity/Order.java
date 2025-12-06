// entity/Order.java
package org.anta.order_service.entity;

import org.anta.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name="reservation_id")
    private Long reservationId;      // id từ product-service

    @Column(length = 1000)
    private String payUrl;           // từ payment-service (Momo)

    @Column(name="created_at", updatable = false, insertable = false,
            columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}

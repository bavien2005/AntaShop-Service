package org.anta.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @Column(name="product_id")
    private Long productId;

    @Column(name="variant_id", nullable = false)
    private Long variantId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal unitPrice;      // đơn giá tại thời điểm đặt

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal lineTotal;      // unitPrice * quantity
}

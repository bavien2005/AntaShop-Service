package org.anta.category_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.anta.category_service.enums.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "productSummaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "product_id" , nullable = false)
    private Long productId;

    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;
    private Integer stock;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE; // 4 trang thai ACTIVE, INACTIVE (ko ban)
    // , OUT_OF_STOCK(het hang), DELETED(da xoa)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt ;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}

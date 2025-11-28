package org.anta.category_service.dto.response;

import lombok.Data;
import org.anta.category_service.enums.Status;

import java.time.LocalDateTime;
@Data
public class ProductSummariesResponse {
    private Long id;
    private Long productId;
    private String name;
    private String slug;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

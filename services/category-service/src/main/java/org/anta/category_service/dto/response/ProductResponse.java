package org.anta.category_service.dto.response;

import lombok.Data;
import org.anta.category_service.enums.Status;

import java.time.LocalDateTime;
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Status status;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package org.anta.category_service.dto.request;

import lombok.Data;
import org.anta.category_service.enums.Status;
@Data
public class ProductSummariesRequest {
    private Long productId;
    private String name;
    private String slug;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Status status;
    private Long categoryId;
}

package org.anta.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {

    private Long id;

    private String name;

    private String brand;

    private String description;

    private Double price;

    private String category;

    private List<String> images;

    private LocalDateTime createdAt;
}

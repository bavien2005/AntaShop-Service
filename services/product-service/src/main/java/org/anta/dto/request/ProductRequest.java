package org.anta.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {


    private String name;

    private String brand;

    private String description;

    private Double price;

    private String category;

    private List<String> images;

    private java.sql.Timestamp createdAt;

}

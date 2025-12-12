package org.anta.order_service.dto.external;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private String mainImage;
}

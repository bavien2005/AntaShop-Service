package org.anta.category_service.mapper;


import org.anta.category_service.dto.request.ProductSummariesRequest;
import org.anta.category_service.dto.response.ProductSummariesResponse;
import org.anta.category_service.entity.ProductSummaries;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductSummariesMapper {
    ProductSummariesResponse toResponse(ProductSummaries entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true )
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stock", source = "stock")
    @Mapping(target = "imageUrl", source = "imageUrl")
    ProductSummaries toEntity(ProductSummariesRequest request);
}

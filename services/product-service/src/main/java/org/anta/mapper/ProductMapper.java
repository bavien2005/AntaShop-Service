package org.anta.mapper;

import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.ProductResponse;
import org.anta.dto.response.ProductVariantResponse;
import org.anta.entity.Product;
import org.anta.entity.ProductVariant;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> entities);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ProductRequest request, @MappingTarget Product product);
}


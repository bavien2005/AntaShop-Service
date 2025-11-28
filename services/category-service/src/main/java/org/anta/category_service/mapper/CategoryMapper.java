package org.anta.category_service.mapper;

import org.anta.category_service.dto.request.CategoryRequest;
import org.anta.category_service.dto.response.CategoryResponse;
import org.anta.category_service.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true) // vẫn để ignore, ta gán thủ công trong service
    @Mapping(target = "productSummaries", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryRequest request);
}

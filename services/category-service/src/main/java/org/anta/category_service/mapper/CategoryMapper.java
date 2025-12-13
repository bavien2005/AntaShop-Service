package org.anta.category_service.mapper;

import org.anta.category_service.dto.request.CategoryRequest;
import org.anta.category_service.dto.response.CategoryResponse;
import org.anta.category_service.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryRequest request);
}

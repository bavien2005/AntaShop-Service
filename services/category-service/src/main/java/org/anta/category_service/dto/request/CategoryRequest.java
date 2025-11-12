package org.anta.category_service.dto.request;

import lombok.Data;
import org.anta.category_service.entity.Category;
@Data
public class CategoryRequest {
    private String name;
    private String slug;
    private String description;
    private Long parentId;
}

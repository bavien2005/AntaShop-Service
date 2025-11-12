package org.anta.category_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.mapper.CategoryMapper;
import org.anta.category_service.repository.CategoryRepository;
import org.anta.category_service.dto.request.CategoryRequest;
import org.anta.category_service.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public Category create(CategoryRequest categoryRequest){
        Category category = categoryMapper.toEntity(categoryRequest);

        // Nếu có parentId thì gán parent
        if (categoryRequest.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public void delete(Long id){
        categoryRepository.deleteById(id);
    }

}

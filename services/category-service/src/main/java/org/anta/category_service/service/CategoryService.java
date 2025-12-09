package org.anta.category_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.mapper.CategoryMapper;
import org.anta.category_service.repository.CategoryRepository;
import org.anta.category_service.dto.request.CategoryRequest;
import org.anta.category_service.entity.Category;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category create(CategoryRequest request) {

        if(categoryRepository.existsBySlug(request.getSlug())){
            throw new RuntimeException("Slug already exists");
        }

        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    public Category update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Kiểm tra slug trùng (trừ chính nó)
        if (categoryRepository.existsBySlug(request.getSlug()) &&
                !category.getSlug().equals(request.getSlug())) {
            throw new RuntimeException("Slug already exists");
        }

        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setTitle(request.getTitle());
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}

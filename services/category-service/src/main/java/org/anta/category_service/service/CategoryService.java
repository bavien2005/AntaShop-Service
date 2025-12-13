//package org.anta.category_service.service;
//
//import lombok.RequiredArgsConstructor;
//import org.anta.category_service.dto.request.CategoryRequest;
//import org.anta.category_service.entity.Category;
//import org.anta.category_service.exception.ConflictException;
//import org.anta.category_service.exception.NotFoundException;
//import org.anta.category_service.mapper.CategoryMapper;
//import org.anta.category_service.repository.CategoryRepository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CategoryService {
//    private final CategoryRepository categoryRepository;
//    private final CategoryMapper categoryMapper;
//
//    @Transactional(readOnly = true)
//    public List<Category> getAll() {
//        return categoryRepository.findAll();
//    }
//
//    @Transactional(readOnly = true)
//    public Page<Category> list(String q, String title, Pageable pageable) {
//        if (title != null && !title.isBlank()) {
//            return categoryRepository.findAllByTitleIgnoreCase(title, pageable);
//        }
//        if (q != null && !q.isBlank()) {
//            return categoryRepository.findByNameContainingIgnoreCaseOrSlugContainingIgnoreCase(q, q, pageable);
//        }
//        return categoryRepository.findAll(pageable);
//    }
//
//    @Transactional
//    public Category create(CategoryRequest request) {
//        if (categoryRepository.existsBySlug(request.getSlug())) {
//            throw new ConflictException("Slug already exists");
//        }
//        Category category = categoryMapper.toEntity(request);
//        return categoryRepository.save(category);
//    }
//
//    @Transactional(readOnly = true)
//    public Category getBySlug(String slug) {
//        return categoryRepository.findBySlug(slug)
//                .orElseThrow(() -> new NotFoundException("Category not found"));
//    }
//
//    @Transactional
//    public Category update(Long id, CategoryRequest request) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Category not found"));
//
//        // Kiểm tra slug trùng (trừ chính nó)
//        if (categoryRepository.existsBySlug(request.getSlug())
//                && !category.getSlug().equals(request.getSlug())) {
//            throw new ConflictException("Slug already exists");
//        }
//
//        categoryMapper.updateEntity(category, request);
//        return categoryRepository.save(category);
//    }
//
//    @Transactional
//    public void delete(Long id) {
//        if (!categoryRepository.existsById(id)) {
//            throw new NotFoundException("Category not found");
//        }
//        categoryRepository.deleteById(id);
//    }
//}
package org.anta.category_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.dto.request.CategoryRequest;
import org.anta.category_service.entity.Category;
import org.anta.category_service.exception.ConflictException;
import org.anta.category_service.exception.NotFoundException;
import org.anta.category_service.mapper.CategoryMapper;
import org.anta.category_service.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public Category create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new ConflictException("Slug already exists");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Page<Category> list(String q, String title, Pageable pageable) {
        if (title != null && !title.isBlank()) {
            return categoryRepository.findAllByTitleIgnoreCase(title, pageable);
        }
        if (q != null && !q.isBlank()) {
            return categoryRepository
                    .findByNameContainingIgnoreCaseOrSlugContainingIgnoreCase(q, q, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Category>> groupedByTitle() {
        return categoryRepository.findAll().stream()
                .collect(Collectors.groupingBy(c ->
                        (c.getTitle() == null ? "" : c.getTitle().toLowerCase())));
    }

    // src/main/java/org/anta/category_service/service/CategoryService.java
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

}

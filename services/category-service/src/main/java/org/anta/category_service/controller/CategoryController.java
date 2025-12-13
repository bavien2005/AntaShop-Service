package org.anta.category_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.dto.request.CategoryRequest;
import org.anta.category_service.dto.response.CategoryResponse;
import org.anta.category_service.entity.Category;
import org.anta.category_service.mapper.CategoryMapper;
import org.anta.category_service.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "title", required = false) String title,
            Pageable pageable
    ) {
        Page<Category> page = categoryService.list(q, title, pageable);
        Page<CategoryResponse> mapped = page.map(categoryMapper::toResponse);
        return ResponseEntity.ok(mapped);
    }
    @GetMapping("/grouped")
    public ResponseEntity<Map<String, List<CategoryResponse>>> grouped() {
        Map<String, List<Category>> grouped = categoryService.groupedByTitle();
        Map<String, List<CategoryResponse>> dto = grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(categoryMapper::toResponse)
                                .collect(Collectors.toList())
                ));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest rq) {
        Category saved = categoryService.create(rq);
        return ResponseEntity.ok(categoryMapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id); // sẽ viết ở bước 2
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        int deletedProducts = categoryService.deleteCategoryAndProducts(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "categoryId", id,
                "deletedProducts", deletedProducts
        ));
    }
}

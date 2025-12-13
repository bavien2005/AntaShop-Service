// src/main/java/org/anta/category_service/controller/CategoryController.java
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
@CrossOrigin(origins = "*") // cho FE gọi trực tiếp qua 8087 hoặc gateway
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /** GET /api/categories?q=&title=&page=&size=  -> phân trang + filter */
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
    /** GET /api/categories/grouped  -> group theo title (men, women, accessories, kids, …) */
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

    /** POST /api/categories  -> tạo mới (Admin dùng) */
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest rq) {
        Category saved = categoryService.create(rq);
        return ResponseEntity.ok(categoryMapper.toResponse(saved));
    }

    // src/main/java/org/anta/category_service/controller/CategoryController.java
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id); // sẽ viết ở bước 2
        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }


    // Nếu chỉ cần “thêm ở admin và hiển thị cho user” thì PUT/DELETE không bắt buộc.
}

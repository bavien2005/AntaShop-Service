package org.anta.category_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.mapper.ProductMapper;
import org.anta.category_service.dto.request.ProductRequest;
import org.anta.category_service.dto.response.ProductResponse;
import org.anta.category_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest req) {
        var product = productService.create(req);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    // lay sp theo danh muc
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable Long categoryId) {
        var list = productService.getByCategory(categoryId)
                .stream().map(productMapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

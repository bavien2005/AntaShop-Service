package org.anta.category_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.dto.request.ProductSummariesRequest;
import org.anta.category_service.mapper.ProductSummariesMapper;
import org.anta.category_service.dto.response.ProductSummariesResponse;
import org.anta.category_service.service.ProductSummariesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productSummaries")
public class ProductSummariesController {
    private final ProductSummariesService productSummariesService;
    private final ProductSummariesMapper productSummariesMapper;

    @PostMapping
    public ResponseEntity<ProductSummariesResponse> addToCate(@RequestBody ProductSummariesRequest req) {
        var product = productSummariesService.addToCate(req);
        return ResponseEntity.ok(productSummariesMapper.toResponse(product));
    }

    // lay sp theo danh muc
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductSummariesResponse>> getByCategory(@PathVariable Long categoryId) {
        var list = productSummariesService.getByCategory(categoryId)
                .stream().map(productSummariesMapper::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productSummariesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

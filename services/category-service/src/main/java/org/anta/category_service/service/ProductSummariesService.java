package org.anta.category_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.dto.request.ProductSummariesRequest;
import org.anta.category_service.entity.ProductSummaries;
import org.anta.category_service.repository.CategoryRepository;
import org.anta.category_service.repository.ProductSummariesRepository;
import org.anta.category_service.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSummariesService {
    private final ProductSummariesRepository productSummariesRepository;

    private final CategoryRepository categoryRepository;

    public List<ProductSummaries> getByCategory(Long categoryId) {
        return productSummariesRepository.findByCategoryId(categoryId);
    }

    public ProductSummaries addToCate(ProductSummariesRequest req){
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ProductSummaries productSummaries = ProductSummaries.builder()
                .category(category)
                .productId(req.getProductId())
                .name(req.getName())
                .slug(req.getSlug())
                .description(req.getDescription())
                .price(req.getPrice())
                .stock(req.getStock()) // Thêm dòng này
                .imageUrl(req.getImageUrl())
                .status(req.getStatus())
                .build();

        return productSummariesRepository.save(productSummaries);
    }

    public void delete(Long id){
        productSummariesRepository.deleteById(id);
    }
}

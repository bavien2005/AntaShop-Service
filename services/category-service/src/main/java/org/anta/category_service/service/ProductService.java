package org.anta.category_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.category_service.repository.CategoryRepository;
import org.anta.category_service.repository.ProductRepository;
import org.anta.category_service.dto.request.ProductRequest;
import org.anta.category_service.entity.Category;
import org.anta.category_service.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product create(ProductRequest req){
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .category(category)
                .name(req.getName())
                .slug(req.getSlug())
                .description(req.getDescription())
                .price(req.getPrice())
                .stock(req.getStock()) // Thêm dòng này
                .imageUrl(req.getImageUrl())
                .status(req.getStatus())
                .build();

        return productRepository.save(product);
    }

    public void delete(Long id){
        productRepository.deleteById(id);
    }
}

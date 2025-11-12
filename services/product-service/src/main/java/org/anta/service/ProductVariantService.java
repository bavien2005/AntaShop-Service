package org.anta.service;

import lombok.RequiredArgsConstructor;
import org.anta.dto.request.ProductVariantRequest;
import org.anta.dto.response.ProductVariantResponse;
import org.anta.entity.Product;
import org.anta.entity.ProductVariant;
import org.anta.exception.InsufficientStockException;
import org.anta.mapper.ProductVariantMapper;
import org.anta.repository.ProductRepository;
import org.anta.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;

    @Transactional(readOnly = true)
    public List<ProductVariantResponse> findByProduct(Long productId) {
        return productVariantMapper.toResponseList(productVariantRepository.findByProductId(productId));
    }

    @Transactional(readOnly = true)
    public ProductVariantResponse getById(Long id) {

        ProductVariant v = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        return productVariantMapper.toResponse(v);
    }

    @Transactional
    public ProductVariantResponse add(ProductVariantRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (req.getSku() != null && productVariantRepository.existsBySku(req.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        ProductVariant entity = productVariantMapper.toEntity(req);

        entity.setProduct(product);

        ProductVariant saved = productVariantRepository.save(entity);

        return productVariantMapper.toResponse(saved);
    }

    @Transactional
    public ProductVariantResponse update(Long id, ProductVariantRequest req) {

        ProductVariant existing = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (req.getProductId() != null && !req.getProductId().equals(existing.getProduct().getId())) {
            Product newProduct = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            existing.setProduct(newProduct);
        }

        productVariantMapper.updateFromRequest(req, existing);

        ProductVariant saved = productVariantRepository.save(existing);
        return productVariantMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {

        if (!productVariantRepository.existsById(id)){
            throw new RuntimeException("Variant not found");
        }
        productVariantRepository.deleteById(id);
    }

    @Transactional
    public void reserveStock(Long variantId, int qty) {
        int updated = productVariantRepository.reduceStockIfAvailable(variantId, qty);
        if (updated == 0) {
            throw new InsufficientStockException("Not enough stock for variant id " + variantId);
        }
    }

    @Transactional
    public void releaseStock(Long variantId, int qty) {
        productVariantRepository.increaseStock(variantId, qty);
    }

}

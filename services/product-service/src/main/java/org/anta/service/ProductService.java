package org.anta.service;


import lombok.extern.slf4j.Slf4j;
import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.ProductResponse;
import org.anta.entity.Product;
import org.anta.entity.ProductVariant;
import org.anta.mapper.ProductMapper;
import org.anta.mapper.ProductVariantMapper;
import org.anta.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.anta.repository.ProductVariantRepository;
import org.anta.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;


    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> {
            ProductResponse response = productMapper.toResponse(product);

            List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
            response.setVariants(productVariantMapper.toResponseList(variants));

            int totalStock = variants.stream()
                    .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
                    .sum();
            response.setTotalStock(totalStock);

            if (response.getImages() != null && !response.getImages().isEmpty()) {
                response.setThumbnail(response.getImages().get(0));
            }

            response.setRating(5);
            response.setSales(0L);

            return response;
        }).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        ProductResponse response = productMapper.toResponse(product);

        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        response.setVariants(productVariantMapper.toResponseList(variants));


        int totalStock = variants.stream()
                .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
                .sum();
        response.setTotalStock(totalStock);


        if (response.getImages() != null && !response.getImages().isEmpty()) {
            response.setThumbnail(response.getImages().get(0));
        }

        response.setRating(5);
        response.setSales(0L);

        return response;
    }



    @Transactional
    public ProductResponse addProduct(ProductRequest productRequest){

        Product entity = productMapper.toEntityWithParents(productRequest);

        if (entity.getImages() == null) {
            entity.setImages(List.of());
        }

        if (entity.getVariants() != null && !entity.getVariants().isEmpty()) {
            int total = entity.getVariants().stream()
                    .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
                    .sum();
            entity.setTotalStock(total);
        } else {
            if (productRequest.getTotalStock() != null) {
                entity.setTotalStock(productRequest.getTotalStock());
            } else {
                entity.setTotalStock(0);
            }
        }

        var saved = productRepository.save(entity);

        ProductResponse resp = productMapper.toResponse(saved);
        if (resp.getImages() != null && !resp.getImages().isEmpty()) {
            resp.setThumbnail(resp.getImages().get(0));
        }
        return resp;
    }

    @Transactional
    public ProductResponse updateProduct(Long id , ProductRequest productRequest){

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        productMapper.updateFromRequest(productRequest, product);
        return productMapper.toResponse(productRepository.save(product));
    }


    @Transactional
    public ProductResponse deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(product);
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductByName(String name) {
        String search = StringUtils.removeAccent(name).toLowerCase();
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> StringUtils.removeAccent(p.getName()).toLowerCase().contains(search))
                .collect(Collectors.toList());
        return productMapper.toResponseList(products);
    }

}

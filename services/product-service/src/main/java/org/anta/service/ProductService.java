package org.anta.service;


import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.ProductResponse;
import org.anta.entity.Product;
import org.anta.mapper.ProductMapper;
import org.anta.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.anta.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional
    public List<ProductResponse> getAllProduct(){
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }


    @Transactional
    public ProductResponse addProduct(ProductRequest productRequest){

        Product entity = productMapper.toEntity(productRequest);

        if (entity.getImages() == null) {
            entity.setImages(List.of());
        }

        var saved = productRepository.save(entity);

        return productMapper.toResponse(saved);
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

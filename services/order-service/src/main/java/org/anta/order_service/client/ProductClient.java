package org.anta.order_service.client;


import org.anta.order_service.dto.external.ProductDto;
import org.anta.order_service.dto.external.VariantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product", url = "${product.base-url}")
public interface ProductClient {

    @GetMapping("/api/productVariant/variant/{id}")
    VariantDto getVariant(@PathVariable("id") Long id);

    // Thêm endpoint lấy product (nếu product-service hỗ trợ)
    @GetMapping("/api/product/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);
}
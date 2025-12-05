package org.anta.controller;

import lombok.extern.slf4j.Slf4j;
import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.ProductResponse;
import org.anta.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProduct(){
        return ResponseEntity.ok(productService.getAllProduct());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest){
        ProductResponse productResponse = productService.addProduct(productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse>  update(@PathVariable Long id , @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id ){
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted id: " +id +" Successfully");
    }


    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductResponse>> getAllProductByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping("/sync-images/{id}")
    public ResponseEntity<ProductResponse> syncImages(@PathVariable Long id) {
        ProductResponse resp = productService.syncImagesFromCloud(id);
        return ResponseEntity.ok(resp);
    }
}

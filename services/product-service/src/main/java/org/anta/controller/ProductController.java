package org.anta.controller;

import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.ProductResponse;
import org.anta.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
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
    public Map<ResponseEntity<ProductResponse> , String> addProduct(@RequestBody ProductRequest productRequest){
        ProductResponse productResponse = productService.addProduct(productRequest);
        return Map.of(ResponseEntity.ok(productResponse) , "Created Successfully\n " +
                "id: "
                + productResponse.getId()
                + " name: " + productResponse.getName());
    }

    @PutMapping("/update/{id}")
    public Map<ProductResponse , String>  update(@PathVariable Long id , @RequestBody ProductRequest productRequest){
        return Map.of(productService.updateProduct(id, productRequest) , "Updated id: " +id +" Successfully");
    }

    @DeleteMapping("/delete/{id}")
    public Map<ProductResponse , String> delete(@PathVariable Long id ){
        return Map.of( productService.deleteProduct(id) , "Deleted id: " +id +" Successfully");
    }


    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductResponse>> getAllProductByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

}

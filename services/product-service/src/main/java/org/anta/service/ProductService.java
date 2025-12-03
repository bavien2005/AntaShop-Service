package org.anta.service;


import lombok.extern.slf4j.Slf4j;
import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.FileMetadataDto;
import org.anta.dto.response.ProductResponse;
import org.anta.entity.Product;
import org.anta.entity.ProductVariant;
import org.anta.mapper.ProductMapper;
import org.anta.mapper.ProductVariantMapper;
import org.anta.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.anta.repository.ProductVariantRepository;
import org.anta.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductVariantRepository productVariantRepository;

    private final ProductVariantMapper productVariantMapper;

    private final RestTemplate restTemplate;

    @Value("${cloud.service.base-url}")
    private String cloudBaseUrl;

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

        // map request -> entity (đảm bảo variants có parent nhờ mapper)
        Product entity = productMapper.toEntityWithParents(productRequest);

        if (entity.getImages() == null) {
            entity.setImages(List.of());
        }

        // set totalStock nếu có variants hoặc request cung cấp
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

        // Lưu product trước để có productId (transaction đang mở)
        Product saved = productRepository.save(entity);

        // Nếu client đưa imageIds => call cloud-service để gắn
        List<Long> imageIds = productRequest.getImageIds();
        if (imageIds != null && !imageIds.isEmpty()) {
            boolean assignedToCloud = false;
            try {
                // 1) Gọi cloud-service để cập nhật productId cho các image
                String assignUrl = cloudBaseUrl + "/update-product/" + saved.getId();
                // restTemplate.put sẽ gửi body dưới dạng JSON (List<Long>)
                restTemplate.put(assignUrl, imageIds);
                assignedToCloud = true;

                // 2) Lấy metadata của product từ cloud để lấy URL thực tế
                String fetchUrl = cloudBaseUrl + "/product/" + saved.getId();
                // dùng FileMetadataDto[] để parse response
                FileMetadataDto[] files = restTemplate.getForObject(fetchUrl,FileMetadataDto[].class);

                if (files != null && files.length > 0) {
                    List<String> urls = Arrays.stream(files)
                            .map(FileMetadataDto::getUrl)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    // cập nhật entity đang managed trong transaction
                    saved.setImages(urls);

                    // nếu muốn gắn thumbnail tự động: lấy isMain true, nếu không có thì first
                    String thumbnail = Arrays.stream(files)
                            .filter(f -> Boolean.TRUE.equals(f.getIsMain()))
                            .map(FileMetadataDto::getUrl)
                            .findFirst()
                            .orElse(urls.size() > 0 ? urls.get(0) : null);

                    // lưu lại để persist images vào DB (entity đang managed nhưng ta gọi save để rõ ràng)
                    productRepository.save(saved);
                } else {
                    // nếu cloud trả về rỗng => coi là lỗi (vì client đã upload nhưng cloud không gắn thành công)
                    throw new RuntimeException("No files returned from cloud for product " + saved.getId());
                }

            } catch (Exception ex) {
                log.error("Failed to assign images on cloud for productId {} : {}",
                        saved.getId(), ex.getMessage(), ex);

                // Bù trừ: nếu đã assign trên cloud nhưng sau đó fetch/ghi DB lỗi,
                // cố gắng unassign (cleanup) để không leave orphan state trên cloud.
                if (assignedToCloud) {
                    try {
                        String unassignUrl = cloudBaseUrl + "/update-product/" + saved.getId();
                        // send empty list to clear product_id for those images
                        restTemplate.put(unassignUrl, List.of());
                    } catch (Exception inner) {
                        log.warn("Failed to rollback cloud assignments for product {} : {}",
                                saved.getId(), inner.getMessage(), inner);
                        // tiếp tục ném lỗi ban đầu; DB transaction sẽ rollback nhưng cloud có thể còn giữ productId
                    }
                }

                // ném exception để transaction rollback (product không được tạo)
                throw new RuntimeException("Failed to associate uploaded images. Product was not created.", ex);
            }
        }

        // Build response bằng mapper
        ProductResponse resp = productMapper.toResponse(saved);
        if (resp.getImages() != null && !resp.getImages().isEmpty()) {
            resp.setThumbnail(resp.getImages().get(0));
        }
        // sales/rating defaults nếu cần
        if (resp.getRating() == null) resp.setRating(5);
        if (resp.getSales() == null) resp.setSales(0L);

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

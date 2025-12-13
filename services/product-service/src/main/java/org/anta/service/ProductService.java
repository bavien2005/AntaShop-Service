package org.anta.service;


import lombok.extern.slf4j.Slf4j;
import org.anta.client.CategoryClient;
import org.anta.client.CategoryResponse;
import org.anta.dto.request.ProductRequest;
import org.anta.dto.request.ProductVariantRequest;
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

import java.math.BigDecimal;
import java.util.*;
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

    private final CategoryClient categoryClient;

    @Value("${cloud.service.base-url}")
    private String cloudBaseUrl;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> {
            ProductResponse response = productMapper.toResponse(product);

            List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
            response.setVariants(productVariantMapper.toResponseList(variants));

            int totalStock;
            if (variants != null && !variants.isEmpty()) {
                totalStock = variants.stream()
                        .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
                        .sum();
            } else {
                totalStock = product.getTotalStock() != null ? product.getTotalStock() : 0;
            }
            response.setTotalStock(totalStock);

            if (response.getImages() != null && !response.getImages().isEmpty()) {
                response.setThumbnail(response.getImages().get(0));
            }

            // set price display
            double displayPrice = computeDisplayPrice(product);
            response.setPrice(BigDecimal.valueOf(displayPrice));

            if (response.getRating() == null) response.setRating(5);
            if (response.getSales() == null) response.setSales(0L);

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

        int totalStock;
        if (variants != null && !variants.isEmpty()) {
            totalStock = variants.stream()
                    .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
                    .sum();
        } else {
            totalStock = product.getTotalStock() != null ? product.getTotalStock() : 0;
        }
        response.setTotalStock(totalStock);


        if (response.getImages() != null && !response.getImages().isEmpty()) {
            response.setThumbnail(response.getImages().get(0));
        }

        double displayPrice = computeDisplayPrice(product);
        response.setPrice(BigDecimal.valueOf(displayPrice));
        if (response.getRating() == null) response.setRating(5);
        if (response.getSales() == null) response.setSales(0L);

        return response;
    }



//    @Transactional
//    public ProductResponse addProduct(ProductRequest productRequest){
//
//        // map request -> entity (đảm bảo variants có parent nhờ mapper)
//        Product entity = productMapper.toEntityWithParents(productRequest);
//
//        if (entity.getImages() == null) {
//            entity.setImages(List.of());
//        }
//
//        // set totalStock nếu có variants hoặc request cung cấp
//        if (entity.getVariants() != null && !entity.getVariants().isEmpty()) {
//            int total = entity.getVariants().stream()
//                    .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
//                    .sum();
//            entity.setTotalStock(total);
//        } else {
//            if (productRequest.getTotalStock() != null) {
//                entity.setTotalStock(productRequest.getTotalStock());
//            } else {
//                entity.setTotalStock(0);
//            }
//        }
//
//        // Lưu product trước để có productId (transaction đang mở)
//        Product saved = productRepository.save(entity);
//
//        // Nếu client đưa imageIds => call cloud-service để gắn
//        List<Long> imageIds = productRequest.getImageIds();
//        if (imageIds != null && !imageIds.isEmpty()) {
//            boolean assignedToCloud = false;
//            try {
//                // 1) Gọi cloud-service để cập nhật productId cho các image
//                String assignUrl = cloudBaseUrl + "/update-product/" + saved.getId();
//                // restTemplate.put sẽ gửi body dưới dạng JSON (List<Long>)
//                restTemplate.put(assignUrl, imageIds);
//                assignedToCloud = true;
//
//                // 2) Lấy metadata của product từ cloud để lấy URL thực tế
//                String fetchUrl = cloudBaseUrl + "/product/" + saved.getId();
//                // dùng FileMetadataDto[] để parse response
//                FileMetadataDto[] files = restTemplate.getForObject(fetchUrl,FileMetadataDto[].class);
//
//                if (files != null && files.length > 0) {
//                    List<String> urls = Arrays.stream(files)
//                            .map(FileMetadataDto::getUrl)
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//
//                    // cập nhật entity đang managed trong transaction
//                    saved.setImages(urls);
//
//                    // nếu muốn gắn thumbnail tự động: lấy isMain true, nếu không có thì first
//                    String thumbnail = Arrays.stream(files)
//                            .filter(f -> Boolean.TRUE.equals(f.getIsMain()))
//                            .map(FileMetadataDto::getUrl)
//                            .findFirst()
//                            .orElse(urls.size() > 0 ? urls.get(0) : null);
//
//                    // lưu lại để persist images vào DB (entity đang managed nhưng ta gọi save để rõ ràng)
//                    productRepository.save(saved);
//                } else {
//                    // nếu cloud trả về rỗng => coi là lỗi (vì client đã upload nhưng cloud không gắn thành công)
//                    throw new RuntimeException("No files returned from cloud for product " + saved.getId());
//                }
//
//            } catch (Exception ex) {
//                log.error("Failed to assign images on cloud for productId {} : {}",
//                        saved.getId(), ex.getMessage(), ex);
//
//                // Bù trừ: nếu đã assign trên cloud nhưng sau đó fetch/ghi DB lỗi,
//                // cố gắng unassign (cleanup) để không leave orphan state trên cloud.
//                if (assignedToCloud) {
//                    try {
//                        String unassignUrl = cloudBaseUrl + "/update-product/" + saved.getId();
//                        // send empty list to clear product_id for those images
//                        restTemplate.put(unassignUrl, List.of());
//                    } catch (Exception inner) {
//                        log.warn("Failed to rollback cloud assignments for product {} : {}",
//                                saved.getId(), inner.getMessage(), inner);
//                        // tiếp tục ném lỗi ban đầu; DB transaction sẽ rollback nhưng cloud có thể còn giữ productId
//                    }
//                }
//
//                // ném exception để transaction rollback (product không được tạo)
//                throw new RuntimeException("Failed to associate uploaded images. Product was not created.", ex);
//            }
//        }
//
//        // Build response bằng mapper
//        ProductResponse resp = productMapper.toResponse(saved);
//        if (resp.getImages() != null && !resp.getImages().isEmpty()) {
//            resp.setThumbnail(resp.getImages().get(0));
//        }
//        double displayPriceAfter = computeDisplayPrice(saved);
//        resp.setPrice(BigDecimal.valueOf(displayPriceAfter));
//        if (resp.getRating() == null) resp.setRating(5);
//        if (resp.getSales() == null) resp.setSales(0L);
//
//        return resp;
//    }\
    @Transactional
    public ProductResponse addProduct(ProductRequest productRequest){

        // =============================
        // 1) Kiểm tra Category nếu có
        // =============================
        if (productRequest.getCategoryId() != null) {
            boolean exists = categoryClient.existsCategory(productRequest.getCategoryId());
            if (!exists) {
                throw new RuntimeException("Category not found with ID: " + productRequest.getCategoryId());
            }
        }

        // =====================================
        // 2) Map request -> entity (có variants)
        // =====================================
        Product entity = productMapper.toEntityWithParents(productRequest);

        // Gán categoryId vào entity
        entity.setCategoryId(productRequest.getCategoryId());

        if (entity.getImages() == null) {
            entity.setImages(List.of());
        }

        // tính totalStock
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

        // =====================================
        // 3) Lưu product trước để có ID
        // =====================================
        Product saved = productRepository.save(entity);

        // =====================================
        // 4) Gắn imageIds với cloud-service
        // =====================================
        List<Long> imageIds = productRequest.getImageIds();
        if (imageIds != null && !imageIds.isEmpty()) {
            boolean assignedToCloud = false;
            try {
                // 1) update productId vào ảnh
                String assignUrl = cloudBaseUrl + "/update-product/" + saved.getId();
                restTemplate.put(assignUrl, imageIds);
                assignedToCloud = true;

                // 2) Fetch metadata từ cloud
                String fetchUrl = cloudBaseUrl + "/product/" + saved.getId();
                FileMetadataDto[] files = restTemplate.getForObject(fetchUrl, FileMetadataDto[].class);

                if (files != null && files.length > 0) {
                    List<String> urls = Arrays.stream(files)
                            .map(FileMetadataDto::getUrl)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    saved.setImages(urls);

                    // thumbnail
                    String thumbnail = Arrays.stream(files)
                            .filter(f -> Boolean.TRUE.equals(f.getIsMain()))
                            .map(FileMetadataDto::getUrl)
                            .findFirst()
                            .orElse(urls.size() > 0 ? urls.get(0) : null);

                    productRepository.save(saved);

                } else {
                    throw new RuntimeException("No files returned from cloud for product " + saved.getId());
                }

            } catch (Exception ex) {
                log.error("FAILED TO ASSIGN IMAGES for productId {} : {}", saved.getId(), ex.getMessage(), ex);

                if (assignedToCloud) {
                    try {
                        String unassignUrl = cloudBaseUrl + "/update-product/" + saved.getId();
                        restTemplate.put(unassignUrl, List.of());
                    } catch (Exception inner) {
                        log.warn("ROLLBACK FAILED for product {} : {}", saved.getId(), inner.getMessage());
                    }
                }

                throw new RuntimeException("Failed to associate images. Product was not created.", ex);
            }
        }

        // =====================================
        // 5) Build response
        // =====================================
        ProductResponse resp = productMapper.toResponse(saved);
        if (resp.getImages() != null && !resp.getImages().isEmpty()) {
            resp.setThumbnail(resp.getImages().get(0));
        }

        double displayPriceAfter = computeDisplayPrice(saved);
        resp.setPrice(BigDecimal.valueOf(displayPriceAfter));

        if (resp.getRating() == null) resp.setRating(5);
        if (resp.getSales() == null) resp.setSales(0L);

        return resp;
    }


    private double computeDisplayPrice(Product product) {
        // Nếu product.getPrice != null và > 0 -> dùng nó
        try {
            if (product.getPrice() != null && product.getPrice().doubleValue() > 0) {
                return product.getPrice().doubleValue();
            }
        } catch (Exception ignored) {}

        // Nếu có variants -> lấy min price
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        Optional<Double> min = variants.stream()
                .map(v -> v.getPrice() == null ? 0.0 : v.getPrice().doubleValue())
                .filter(p -> p != null && p > 0)
                .min(Double::compareTo);
        return min.orElse(0.0);
    }


    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // 1) cập nhật các field cơ bản (không chạm variants collection structure)
        productMapper.updateFromRequest(productRequest, product);

        // 2) load existing variants from DB (we will operate on product.getVariants() - the managed collection)
        List<ProductVariant> managed = product.getVariants();
        if (managed == null) {
            managed = new ArrayList<>();
            product.setVariants(managed); // set the managed collection if null
        }

        // Build helper maps from managed collection
        Map<Long, ProductVariant> managedById = managed.stream()
                .filter(v -> v.getId() != null)
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        Map<String, ProductVariant> managedBySku = managed.stream()
                .filter(v -> v.getSku() != null)
                .collect(Collectors.toMap(ProductVariant::getSku, v -> v));

        // Prepare list of incoming variants (from request) mapped to actions
        List<ProductVariant> incoming = new ArrayList<>();
        if (productRequest.getVariants() != null) {
            for (ProductVariantRequest vReq : productRequest.getVariants()) {
                ProductVariant match = null;
                if (vReq.getId() != null) {
                    match = managedById.get(vReq.getId());
                }
                if (match == null && vReq.getSku() != null) {
                    match = managedBySku.get(vReq.getSku());
                }

                if (match != null) {
                    // update fields on the managed entity (don't replace the object)
                    productVariantMapper.updateFromRequest(vReq, match);
                    incoming.add(match);
                    // remove from helper maps so remaining ones are deletions
                    if (match.getId() != null) managedById.remove(match.getId());
                    if (match.getSku() != null) managedBySku.remove(match.getSku());
                } else {
                    // create new variant (transient) and set parent
                    ProductVariant nv = productVariantMapper.toEntity(vReq);
                    nv.setProduct(product);
                    incoming.add(nv);
                }
            }
        }

        // 3) Remove orphans from the managed collection (those left in managedById / managedBySku)
        Set<Long> idsToRemove = new HashSet<>(managedById.keySet());
        // iterate managed collection and remove items whose id is in idsToRemove OR not present in incoming (by id/sku)
        Iterator<ProductVariant> it = managed.iterator();
        while (it.hasNext()) {
            ProductVariant mv = it.next();
            boolean shouldRemove = false;
            if (mv.getId() != null && idsToRemove.contains(mv.getId())) {
                shouldRemove = true;
            } else {
                // also check if this managed item is not present in incoming by id/sku
                boolean presentInIncoming = incoming.stream().anyMatch(i ->
                        (i.getId() != null && i.getId().equals(mv.getId())) ||
                                (i.getSku() != null && i.getSku().equals(mv.getSku()))
                );
                if (!presentInIncoming) shouldRemove = true;
            }
            if (shouldRemove) {
                it.remove(); // this triggers orphan removal when transaction flushes
            }
        }

        // 4) Add new incoming variants to managed collection (only those without id or not already present)
        for (ProductVariant inv : incoming) {
            boolean exists = false;
            if (inv.getId() != null) {
                exists = managed.stream().anyMatch(m -> Objects.equals(m.getId(), inv.getId()));
            } else if (inv.getSku() != null) {
                exists = managed.stream().anyMatch(m -> Objects.equals(m.getSku(), inv.getSku()));
            }
            if (!exists) {
                // ensure parent set
                inv.setProduct(product);
                managed.add(inv);
            }
        }

        // 5) (Optional) validate SKU uniqueness globally before flush/save
        for (ProductVariant v : managed) {
            if (v.getSku() == null) continue;
            Optional<ProductVariant> found = productVariantRepository.findBySku(v.getSku());
            if (found.isPresent() && v.getId() != null && !Objects.equals(found.get().getId(), v.getId())) {
                throw new RuntimeException("SKU already exists: " + v.getSku());
            }
            if (found.isPresent() && v.getId() == null && !Objects.equals(found.get().getProduct().getId(), product.getId())) {
                throw new RuntimeException("SKU already exists in another product: " + v.getSku());
            }
        }

        // 6) recompute totalStock from managed collection
        int total = managed.stream().mapToInt(v -> v.getStock() == null ? 0 : v.getStock()).sum();
        product.setTotalStock(total);

        // 7) save product (Hibernate will detect adds/updates/deletes on managed collection and act accordingly)
        Product saved = productRepository.save(product);

        // build response (same as before)
        ProductResponse resp = productMapper.toResponse(saved);
        List<ProductVariant> variants = productVariantRepository.findByProductId(saved.getId());
        resp.setVariants(productVariantMapper.toResponseList(variants));
        resp.setTotalStock(saved.getTotalStock() != null ? saved.getTotalStock() : 0);
        if (resp.getImages() != null && !resp.getImages().isEmpty()) resp.setThumbnail(resp.getImages().get(0));
        double displayPrice = computeDisplayPrice(saved);
        resp.setPrice(BigDecimal.valueOf(displayPrice));
        if (resp.getRating() == null) resp.setRating(5);
        if (resp.getSales() == null) resp.setSales(0L);

        return resp;
    }



    @Transactional
    public ProductResponse deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // 1) cố gắng lấy files liên quan trên cloud
        try {
            String fetchUrl = cloudBaseUrl + "/product/" + id;
            FileMetadataDto[] files = restTemplate.getForObject(fetchUrl, FileMetadataDto[].class);
            if (files != null && files.length > 0) {
                for (FileMetadataDto f : files) {
                    try {
                        // giả định cloud service expose DELETE /api/cloud/file/{id}
                        String deleteUrl = cloudBaseUrl + "/file/" + f.getId();
                        restTemplate.delete(deleteUrl);
                    } catch (Exception e) {
                        log.warn("Failed to delete file {} on cloud for product {} : {}", f.getId(), id, e.getMessage());
                        // tiếp tục để cố xóa các file khác / xóa product
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to fetch files from cloud for product {} : {}", id, ex.getMessage());
            // không ném, vẫn tiếp tục xóa product (tùy business logic bạn có thể ném)
        }

        // 2) xóa product (và cascade/variant nếu DB cấu hình)
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


    @Transactional
    public ProductResponse syncImagesFromCloud(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        try {
            String fetchUrl = cloudBaseUrl + "/product/" + productId;
            // 1) Try mapping to DTO[]
            FileMetadataDto[] files = null;
            try {
                files = restTemplate.getForObject(fetchUrl, FileMetadataDto[].class);
            } catch (Exception e) {
                log.warn("Failed to map cloud response to FileMetadataDto[]: {}", e.getMessage());
            }

            // 2) If files null, try generic Map[] and extract url/public_id
            List<String> urls;
            if (files != null && files.length > 0) {
                urls = Arrays.stream(files)
                        .map(f -> {
                            if (f.getUrl() != null) return f.getUrl();
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else {
                // fallback: fetch as Map[]
                try {
                    Object[] raw = restTemplate.getForObject(fetchUrl, Object[].class);
                    urls = Arrays.stream(raw != null ? raw : new Object[0])
                            .map(o -> {
                                try {
                                    @SuppressWarnings("unchecked")
                                    java.util.Map<String, Object> m = (java.util.Map<String, Object>) o;
                                    // common Cloudinary field names
                                    if (m.get("url") != null) return String.valueOf(m.get("url"));
                                    if (m.get("secure_url") != null) return String.valueOf(m.get("secure_url"));
                                    if (m.get("publicUrl") != null) return String.valueOf(m.get("publicUrl"));
                                    if (m.get("path") != null) return String.valueOf(m.get("path"));
                                } catch (Exception ex) {
                                    // ignore
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                } catch (Exception ex) {
                    log.error("Failed to fetch raw files from cloud for product {} : {}", productId, ex.getMessage());
                    urls = List.of();
                }
            }

            if (urls == null || urls.isEmpty()) {
                throw new RuntimeException("No files returned from cloud for product " + productId);
            }

            // update product entity images
            product.setImages(urls);
            // persist
            Product saved = productRepository.save(product);

            // build response similar to getProductById
            ProductResponse resp = productMapper.toResponse(saved);

            List<ProductVariant> variants = productVariantRepository.findByProductId(saved.getId());
            resp.setVariants(productVariantMapper.toResponseList(variants));

            int totalStock;
            if (variants != null && !variants.isEmpty()) {
                totalStock = variants.stream().mapToInt(v -> v.getStock() == null ? 0 : v.getStock()).sum();
            } else {
                totalStock = saved.getTotalStock() != null ? saved.getTotalStock() : 0;
            }
            resp.setTotalStock(totalStock);

            // pick thumbnail: try isMain from DTO[] else first url
            String thumbnail = null;
            if (files != null && files.length > 0) {
                thumbnail = Arrays.stream(files)
                        .filter(f -> Boolean.TRUE.equals(f.getIsMain()))
                        .map(FileMetadataDto::getUrl)
                        .findFirst()
                        .orElse(null);
            }
            if (thumbnail == null && !urls.isEmpty()) thumbnail = urls.get(0);
            resp.setThumbnail(thumbnail);

            double displayPrice = computeDisplayPrice(saved);
            resp.setPrice(BigDecimal.valueOf(displayPrice));
            if (resp.getRating() == null) resp.setRating(5);
            if (resp.getSales() == null) resp.setSales(0L);

            return resp;

        } catch (Exception ex) {
            log.error("Failed to sync images from cloud for product {} : {}", productId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to sync images from cloud: " + ex.getMessage(), ex);
        }
    }

    //them san pham vào danh mục
    /**
     * Gán sản phẩm vào danh mục
     */
    public Product assignCategory(Long productId, Long categoryId) {

        // 1) Kiểm tra Category tồn tại trong Category-Service
        if (!categoryClient.existsCategory(categoryId)) {
            throw new RuntimeException("Category not found: " + categoryId);
        }

        // 2) Lấy product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // 3) Gán category
        product.setCategoryId(categoryId);

        // 4) Lưu DB
        return productRepository.save(product);
    }

    /**
     * Xóa sản phẩm khỏi danh mục
     */
    public Product removeCategory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setCategoryId(null);

        return productRepository.save(product);
    }


    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String q) {
        if (q == null || q.trim().isEmpty()) {
            // nếu không có query, trả về tất cả (hoặc trả về [])
            return getAllProduct();
        }

        String keyword = q.trim();

        List<Product> products = productRepository.searchFullTextLoose(keyword);

        return products.stream().map(product -> {
            ProductResponse response = productMapper.toResponse(product);

            List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
            response.setVariants(productVariantMapper.toResponseList(variants));

            int totalStock;
            if (variants != null && !variants.isEmpty()) {
                totalStock = variants.stream().mapToInt(v -> v.getStock() == null ? 0 : v.getStock()).sum();
            } else {
                totalStock = product.getTotalStock() != null ? product.getTotalStock() : 0;
            }
            response.setTotalStock(totalStock);

            if (response.getImages() != null && !response.getImages().isEmpty()) {
                response.setThumbnail(response.getImages().get(0));
            }

            double displayPrice = computeDisplayPrice(product);
            response.setPrice(BigDecimal.valueOf(displayPrice));

            if (response.getRating() == null) response.setRating(5);
            if (response.getSales() == null) response.setSales(0L);

            return response;
        }).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ProductResponse> listByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream().map(p -> {
            ProductResponse r = productMapper.toResponse(p);

            List<ProductVariant> vars = productVariantRepository.findByProductId(p.getId());
            r.setVariants(productVariantMapper.toResponseList(vars));

            int total = (vars == null || vars.isEmpty())
                    ? (p.getTotalStock() == null ? 0 : p.getTotalStock())
                    : vars.stream().mapToInt(v -> v.getStock() == null ? 0 : v.getStock()).sum();
            r.setTotalStock(total);

            if (r.getImages() != null && !r.getImages().isEmpty()) r.setThumbnail(r.getImages().get(0));

            double displayPrice = computeDisplayPrice(p);
            r.setPrice(java.math.BigDecimal.valueOf(displayPrice));
            if (r.getRating() == null) r.setRating(5);
            if (r.getSales() == null) r.setSales(0L);
            return r;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllFiltered(String title, String categorySlug) {
        List<Product> products;

        if (title != null && categorySlug != null) {
            Optional<Long> catIdOpt = categoryClient.resolveCategoryId(title, categorySlug);
            products = catIdOpt.map(productRepository::findByCategoryId).orElse(List.of());
        } else if (title != null) {
            Map<String, List<CategoryResponse>> grouped = categoryClient.getGrouped();
            List<CategoryResponse> cats = grouped.getOrDefault(title, grouped.get(title.toLowerCase()));
            if (cats == null || cats.isEmpty()) return List.of();
            products = cats.stream()
                    .flatMap(c -> productRepository.findByCategoryId(c.getId()).stream())
                    .toList();
        } else if (categorySlug != null) {
            Map<String, List<CategoryResponse>> grouped = categoryClient.getGrouped();
            Optional<Long> catIdOpt = grouped.values().stream()
                    .flatMap(List::stream)
                    .filter(c -> c.getSlug()!=null && c.getSlug().equalsIgnoreCase(categorySlug))
                    .map(CategoryResponse::getId)
                    .findFirst();
            products = catIdOpt.map(productRepository::findByCategoryId).orElse(List.of());
        } else {
            products = productRepository.findAll();
        }

        // map -> ProductResponse (tính variants/stock/price/thumbnail như bạn đang làm)
        return products.stream().map(p -> {
            ProductResponse r = productMapper.toResponse(p);
            List<ProductVariant> vars = productVariantRepository.findByProductId(p.getId());
            r.setVariants(productVariantMapper.toResponseList(vars));
            int total = (vars==null||vars.isEmpty())
                    ? (p.getTotalStock()==null?0:p.getTotalStock())
                    : vars.stream().mapToInt(v -> v.getStock()==null?0:v.getStock()).sum();
            r.setTotalStock(total);
            if (r.getImages()!=null && !r.getImages().isEmpty()) r.setThumbnail(r.getImages().get(0));
            r.setPrice(BigDecimal.valueOf(computeDisplayPrice(p)));
            if (r.getRating()==null) r.setRating(5);
            if (r.getSales()==null) r.setSales(0L);
            return r;
        }).toList();
    }



}

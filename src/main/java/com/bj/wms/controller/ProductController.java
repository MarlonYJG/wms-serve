package com.bj.wms.controller;

import com.bj.wms.entity.Product;
import com.bj.wms.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * 
 * 处理商品相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /**
     * 创建商品
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取商品列表（分页）
     * GET /api/products?page=0&size=10&sort=id,desc
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据ID获取商品
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据商品编码获取商品
     * GET /api/products/code/{productCode}
     */
    @GetMapping("/code/{productCode}")
    public ResponseEntity<?> getProductByCode(@PathVariable String productCode) {
        return productService.getProductByCode(productCode)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 搜索商品
     * GET /api/products/search?keyword=手机&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据分类获取商品
     * GET /api/products/category/{category}?page=0&size=10
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据品牌获取商品
     * GET /api/products/brand/{brand}?page=0&size=10
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<Page<Product>> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByBrand(brand, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据价格范围获取商品
     * GET /api/products/price-range?minPrice=100&maxPrice=1000&page=0&size=10
     */
    @GetMapping("/price-range")
    public ResponseEntity<Page<Product>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 获取库存不足的商品
     * GET /api/products/low-stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        List<Product> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 获取库存过多的商品
     * GET /api/products/over-stock
     */
    @GetMapping("/over-stock")
    public ResponseEntity<List<Product>> getOverStockProducts() {
        List<Product> products = productService.getOverStockProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 更新商品信息
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新商品库存
     * PUT /api/products/{id}/stock
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> stockData) {
        try {
            Integer quantity = stockData.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "库存数量不能为空"));
            }
            
            Product updatedProduct = productService.updateStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("更新库存失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 增加商品库存
     * PUT /api/products/{id}/stock/increase
     */
    @PutMapping("/{id}/stock/increase")
    public ResponseEntity<?> increaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> stockData) {
        try {
            Integer quantity = stockData.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "增加数量不能为空"));
            }
            
            Product updatedProduct = productService.increaseStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("增加库存失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 减少商品库存
     * PUT /api/products/{id}/stock/decrease
     */
    @PutMapping("/{id}/stock/decrease")
    public ResponseEntity<?> decreaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> stockData) {
        try {
            Integer quantity = stockData.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "减少数量不能为空"));
            }
            
            Product updatedProduct = productService.decreaseStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("减少库存失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除商品
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "商品删除成功"));
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}



package com.bj.wms.service;

import com.bj.wms.entity.Product;
import com.bj.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 商品服务类
 * 
 * 处理商品相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 创建商品
     */
    @Transactional
    public Product createProduct(Product product) {
        log.info("创建商品: {}", product.getProductCode());
        
        // 检查商品编码是否已存在
        if (productRepository.existsByProductCode(product.getProductCode())) {
            throw new RuntimeException("商品编码已存在: " + product.getProductCode());
        }
        
        return productRepository.save(product);
    }

    /**
     * 更新商品信息
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        log.info("更新商品: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
        
        // 检查商品编码是否被其他商品使用
        if (!product.getProductCode().equals(productDetails.getProductCode()) && 
            productRepository.existsByProductCode(productDetails.getProductCode())) {
            throw new RuntimeException("商品编码已存在: " + productDetails.getProductCode());
        }
        
        // 更新商品信息
        product.setProductCode(productDetails.getProductCode());
        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setSpecification(productDetails.getSpecification());
        product.setUnit(productDetails.getUnit());
        product.setPrice(productDetails.getPrice());
        product.setMinStock(productDetails.getMinStock());
        product.setMaxStock(productDetails.getMaxStock());
        product.setStatus(productDetails.getStatus());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        
        return productRepository.save(product);
    }

    /**
     * 根据ID获取商品
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 根据商品编码获取商品
     */
    public Optional<Product> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }

    /**
     * 获取所有商品（分页）
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAllActive(pageable);
    }

    /**
     * 根据关键词搜索商品
     */
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 根据分类获取商品
     */
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    /**
     * 根据品牌获取商品
     */
    public Page<Product> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrand(brand, pageable);
    }

    /**
     * 根据状态获取商品
     */
    public Page<Product> getProductsByStatus(Integer status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }

    /**
     * 根据价格范围获取商品
     */
    public Page<Product> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    /**
     * 获取库存不足的商品
     */
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    /**
     * 获取库存过多的商品
     */
    public List<Product> getOverStockProducts() {
        return productRepository.findOverStockProducts();
    }

    /**
     * 删除商品（逻辑删除）
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("删除商品: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
        
        product.setDeleted(1);
        productRepository.save(product);
    }

    /**
     * 更新商品库存
     */
    @Transactional
    public Product updateStock(Long productId, Integer quantity) {
        log.info("更新商品库存: {} -> {}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
        
        if (quantity < 0) {
            throw new RuntimeException("库存数量不能为负数");
        }
        
        product.setStockQuantity(quantity);
        return productRepository.save(product);
    }

    /**
     * 增加商品库存
     */
    @Transactional
    public Product increaseStock(Long productId, Integer quantity) {
        log.info("增加商品库存: {} -> +{}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
        
        if (quantity <= 0) {
            throw new RuntimeException("增加数量必须大于0");
        }
        
        product.setStockQuantity(product.getStockQuantity() + quantity);
        return productRepository.save(product);
    }

    /**
     * 减少商品库存
     */
    @Transactional
    public Product decreaseStock(Long productId, Integer quantity) {
        log.info("减少商品库存: {} -> -{}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
        
        if (quantity <= 0) {
            throw new RuntimeException("减少数量必须大于0");
        }
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("库存不足，当前库存: " + product.getStockQuantity());
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }
}



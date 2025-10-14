package com.bj.wms.controller;

import com.bj.wms.dto.ProductSkuDTO;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.mapper.ProductSkuMapper;
import com.bj.wms.service.ProductSkuService;
import com.bj.wms.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product-sku")
@RequiredArgsConstructor
public class ProductSkuController {

    private final ProductSkuService productSkuService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "skuCode", required = false) String skuCode,
            @RequestParam(value = "name", required = false) String skuName,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "supplierId", required = false) Long supplierId,
            @RequestParam(value = "isBatchManaged", required = false) Boolean isBatchManaged,
            @RequestParam(value = "isExpiryManaged", required = false) Boolean isExpiryManaged
    ) {
        Page<ProductSku> result = productSkuService.page(page, size, skuCode, skuName, supplierId, isBatchManaged, isExpiryManaged, brand, categoryId);
        List<ProductSkuDTO> content = result.getContent().stream()
                .map(ProductSkuMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        ProductSku sku = productSkuService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品SKU不存在"));
        return ResponseUtil.success(ProductSkuMapper.toDTO(sku));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductSkuDTO body) {
        ProductSku toCreate = ProductSkuMapper.toEntity(body);
        ProductSku created = productSkuService.create(toCreate);
        return ResponseUtil.created(ProductSkuMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody ProductSkuDTO body) {
        ProductSku updates = ProductSkuMapper.toEntity(body);
        ProductSku updated = productSkuService.update(id, updates);
        return ResponseUtil.success(ProductSkuMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        productSkuService.delete(id);
        return ResponseUtil.successMsg("删除成功");
    }
}



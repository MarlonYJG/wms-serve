package com.bj.wms.controller;

import com.bj.wms.dto.SupplierDTO;
import com.bj.wms.entity.Supplier;
import com.bj.wms.mapper.SupplierMapper;
import com.bj.wms.service.SupplierService;
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
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "supplierName", required = false) String supplierName,
            @RequestParam(value = "supplierCode", required = false) String supplierCode,
            @RequestParam(value = "isEnabled", required = false) Boolean isEnabled
    ) {
        Page<Supplier> result = supplierService.page(page, size, keyword, supplierName, supplierCode, isEnabled);
        List<SupplierDTO> content = result.getContent().stream()
                .map(SupplierMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));
        return ResponseUtil.success(SupplierMapper.toDTO(supplier));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SupplierDTO body) {
        Supplier created = supplierService.create(SupplierMapper.toEntity(body));
        return ResponseUtil.created(SupplierMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody SupplierDTO body) {
        Supplier updated = supplierService.update(id, SupplierMapper.toEntity(body));
        return ResponseUtil.success(SupplierMapper.toDTO(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isEnabled = body.get("isEnabled");
        if (isEnabled == null) {
            return ResponseUtil.error("isEnabled 不能为空");
        }
        Supplier updated = supplierService.updateStatus(id, isEnabled);
        return ResponseUtil.success(SupplierMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseUtil.successMsg("删除成功");
    }
}



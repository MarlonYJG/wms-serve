package com.bj.wms.controller;

import com.bj.wms.entity.Warehouse;
import com.bj.wms.service.WarehouseService;
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
 * 仓库控制器
 * 
 * 处理仓库相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * 创建仓库
     * POST /api/warehouses
     */
    @PostMapping
    public ResponseEntity<?> createWarehouse(@Valid @RequestBody Warehouse warehouse) {
        try {
            Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouse);
        } catch (Exception e) {
            log.error("创建仓库失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取仓库列表（分页）
     * GET /api/warehouses?page=0&size=10&sort=id,desc
     */
    @GetMapping
    public ResponseEntity<Page<Warehouse>> getAllWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Warehouse> warehouses = warehouseService.getAllWarehouses(pageable);
        return ResponseEntity.ok(warehouses);
    }

    /**
     * 根据ID获取仓库
     * GET /api/warehouses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(warehouse -> ResponseEntity.ok(warehouse))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据仓库编码获取仓库
     * GET /api/warehouses/code/{warehouseCode}
     */
    @GetMapping("/code/{warehouseCode}")
    public ResponseEntity<?> getWarehouseByCode(@PathVariable String warehouseCode) {
        return warehouseService.getWarehouseByCode(warehouseCode)
                .map(warehouse -> ResponseEntity.ok(warehouse))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 搜索仓库
     * GET /api/warehouses/search?keyword=北京&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Warehouse>> searchWarehouses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Warehouse> warehouses = warehouseService.searchWarehouses(keyword, pageable);
        return ResponseEntity.ok(warehouses);
    }

    /**
     * 根据状态获取仓库
     * GET /api/warehouses/status/{status}?page=0&size=10
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Warehouse>> getWarehousesByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Warehouse> warehouses = warehouseService.getWarehousesByStatus(status, pageable);
        return ResponseEntity.ok(warehouses);
    }

    /**
     * 根据负责人获取仓库
     * GET /api/warehouses/manager/{manager}?page=0&size=10
     */
    @GetMapping("/manager/{manager}")
    public ResponseEntity<Page<Warehouse>> getWarehousesByManager(
            @PathVariable String manager,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Warehouse> warehouses = warehouseService.getWarehousesByManager(manager, pageable);
        return ResponseEntity.ok(warehouses);
    }

    /**
     * 获取所有启用的仓库
     * GET /api/warehouses/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Warehouse>> getActiveWarehouses() {
        List<Warehouse> warehouses = warehouseService.getActiveWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    /**
     * 获取容量使用率高的仓库
     * GET /api/warehouses/high-usage?threshold=80.0
     */
    @GetMapping("/high-usage")
    public ResponseEntity<List<Warehouse>> getWarehousesWithHighUsage(
            @RequestParam(defaultValue = "80.0") Double threshold) {
        List<Warehouse> warehouses = warehouseService.getWarehousesWithHighUsage(threshold);
        return ResponseEntity.ok(warehouses);
    }

    /**
     * 更新仓库信息
     * PUT /api/warehouses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable Long id, @Valid @RequestBody Warehouse warehouseDetails) {
        try {
            Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, warehouseDetails);
            return ResponseEntity.ok(updatedWarehouse);
        } catch (Exception e) {
            log.error("更新仓库失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新仓库使用容量
     * PUT /api/warehouses/{id}/capacity
     */
    @PutMapping("/{id}/capacity")
    public ResponseEntity<?> updateUsedCapacity(
            @PathVariable Long id,
            @RequestBody Map<String, Double> capacityData) {
        try {
            Double usedCapacity = capacityData.get("usedCapacity");
            if (usedCapacity == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "使用容量不能为空"));
            }
            
            Warehouse updatedWarehouse = warehouseService.updateUsedCapacity(id, usedCapacity);
            return ResponseEntity.ok(updatedWarehouse);
        } catch (Exception e) {
            log.error("更新使用容量失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 计算仓库使用率
     * GET /api/warehouses/{id}/usage-rate
     */
    @GetMapping("/{id}/usage-rate")
    public ResponseEntity<?> calculateUsageRate(@PathVariable Long id) {
        try {
            Double usageRate = warehouseService.calculateUsageRate(id);
            return ResponseEntity.ok(Map.of("usageRate", usageRate));
        } catch (Exception e) {
            log.error("计算使用率失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除仓库
     * DELETE /api/warehouses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);
            return ResponseEntity.ok(Map.of("message", "仓库删除成功"));
        } catch (Exception e) {
            log.error("删除仓库失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}



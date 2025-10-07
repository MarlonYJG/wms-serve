package com.bj.wms.controller;

import com.bj.wms.entity.Warehouse;
import com.bj.wms.service.WarehouseService;
import com.bj.wms.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDir", required = false) String sortDir,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "isEnabled", required = false) Boolean isEnabled
    ) {
        Page<Warehouse> result = warehouseService.page(page, size, sortBy, sortDir, keyword, name, code, isEnabled);
        Map<String, Object> data = new HashMap<>();
        data.put("data", result.getContent());
        data.put("total", result.getTotalElements());
        return ResponseUtil.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        return ResponseUtil.success(warehouse);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody Warehouse warehouse) {
        Warehouse created = warehouseService.create(warehouse);
        return ResponseUtil.created(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody Warehouse warehouse) {
        Warehouse updated = warehouseService.update(id, warehouse);
        return ResponseUtil.success(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseUtil.successMsg("删除成功");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isEnabled = body.get("isEnabled");
        if (isEnabled == null) {
            throw new IllegalArgumentException("isEnabled 不能为空");
        }
        Warehouse updated = warehouseService.updateStatus(id, isEnabled);
        return ResponseUtil.success(updated);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable Long id) {
        // 占位实现：返回零值统计，后续可基于库存/库区/订单表计算
        warehouseService.getById(id).orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLocations", 0);
        stats.put("occupiedLocations", 0);
        stats.put("totalInventory", 0);
        stats.put("totalValue", 0);
        stats.put("inboundOrders", 0);
        stats.put("outboundOrders", 0);
        return ResponseUtil.success(stats);
    }
}



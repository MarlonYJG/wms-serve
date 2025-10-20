package com.bj.wms.controller;

import com.bj.wms.dto.InventoryCountDTO;
import com.bj.wms.dto.InventoryCountItemDTO;
import com.bj.wms.service.InventoryCountService;
import com.bj.wms.service.InventoryService;
import com.bj.wms.util.PageResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory/counts")
@RequiredArgsConstructor
public class InventoryCountController {

    private final InventoryCountService countService;
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<PageResult<InventoryCountDTO>> page(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer status
    ) {
        return ResponseEntity.ok(countService.page(page, size, warehouseId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryCountDTO> detail(@PathVariable Long id) {
        return ResponseEntity.ok(countService.toDetail(id));
    }

    @PostMapping
    public ResponseEntity<InventoryCountDTO> create(@RequestBody CreateBody body) {
        return ResponseEntity.ok(countService.create(body.getWarehouseId(), body.getRemark(), body.getLocationIds()));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<InventoryCountDTO> start(@PathVariable Long id) {
        return ResponseEntity.ok(countService.start(id));
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<InventoryCountDTO> submit(@PathVariable Long id, @RequestBody List<InventoryCountItemDTO> items) {
        return ResponseEntity.ok(countService.submit(id, items));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<InventoryCountDTO> complete(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean autoAdjust) {
        return ResponseEntity.ok(countService.complete(id, autoAdjust, inventoryService));
    }

    @Data
    public static class CreateBody {
        private Long warehouseId;
        private String remark;
        private java.util.List<Long> locationIds;
    }
}



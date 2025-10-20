package com.bj.wms.controller;

import com.bj.wms.dto.InventoryDTO;
import com.bj.wms.entity.Inventory;
import com.bj.wms.service.InventoryService;
import com.bj.wms.util.PageResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<PageResult<InventoryDTO>> page(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long productSkuId,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false) String batchNo,
            @RequestParam(required = false) Boolean hasStock
    ) {
        PageResult<InventoryDTO> result = inventoryService.page(page, size, warehouseId, locationId, productSkuId, skuCode, batchNo, hasStock);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> detail(@PathVariable Long id) {
        return ResponseEntity.of(inventoryService.getById(id));
    }

    @PostMapping("/{id}/adjust")
    public ResponseEntity<Inventory> adjust(@PathVariable Long id, @RequestBody AdjustBody body) {
        Inventory updated = inventoryService.adjust(id, body.getQuantity(), body.getReason());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<Inventory> transfer(@PathVariable Long id, @RequestBody TransferBody body) {
        Inventory updated = inventoryService.transfer(id, body.getToLocationId(), body.getQuantity(), body.getReason());
        return ResponseEntity.ok(updated);
    }

    @Data
    public static class AdjustBody {
        private int quantity;
        private String reason;
    }

    @Data
    public static class TransferBody {
        private Long toLocationId;
        private int quantity;
        private String reason;
    }
}



package com.bj.wms.controller;

import com.bj.wms.dto.InventoryDTO;
import com.bj.wms.entity.Inventory;
import com.bj.wms.service.InventoryService;
import com.bj.wms.util.PageResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
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
            @RequestParam(required = false) Boolean hasStock,
            @RequestParam(required = false) String zoneType
    ) {
        PageResult<InventoryDTO> result = inventoryService.page(page, size, warehouseId, locationId, productSkuId, skuCode, batchNo, hasStock, zoneType);
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

    /**
     * 根据商品SKU ID和仓库ID查询库存
     */
    @GetMapping("/product/{productSkuId}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByProductSku(
            @PathVariable Long productSkuId,
            @RequestParam(required = false) Long warehouseId) {
        List<InventoryDTO> result = inventoryService.getInventoryByProductSku(productSkuId, warehouseId);
        return ResponseEntity.ok(result);
    }

    @Data
    public static class AdjustBody {
        private Integer quantity;
        private String reason;
    }

    @Data
    public static class TransferBody {
        private Long toLocationId;
        private Integer quantity;
        private String reason;
    }
}
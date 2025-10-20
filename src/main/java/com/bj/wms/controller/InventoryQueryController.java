package com.bj.wms.controller;

import com.bj.wms.dto.InventoryTransactionDTO;
import com.bj.wms.service.InventoryQueryService;
import com.bj.wms.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/transactions")
@RequiredArgsConstructor
public class InventoryQueryController {

    private final InventoryQueryService inventoryQueryService;

    @GetMapping
    public ResponseEntity<PageResult<InventoryTransactionDTO>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long productSkuId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Integer transactionType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        PageResult<InventoryTransactionDTO> result = inventoryQueryService.transactions(page, size, productSkuId, warehouseId, locationId, transactionType, startTime, endTime);
        return ResponseEntity.ok(result);
    }
}



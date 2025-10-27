package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.InboundOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/inbound-order")
@RequiredArgsConstructor
public class InboundOrderController {

    private final InboundOrderService orderService;

    /**
     * 分页查询入库单列表
     */
    @GetMapping
    public ResponseEntity<Page<InboundOrderDTO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        InboundOrderQueryRequest request = new InboundOrderQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setOrderNo(orderNo);
        request.setWarehouseId(warehouseId);
        request.setSupplierId(supplierId);
        // 将字符串类型的status转换为Integer
        if (status != null && !status.trim().isEmpty()) {
            try {
                request.setStatus(Integer.parseInt(status));
            } catch (NumberFormatException e) {
                log.warn("Invalid status format: {}", status);
            }
        }
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        
        Page<InboundOrderDTO> result = orderService.getOrderList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取入库单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<InboundOrderDTO> getOrderDetail(@PathVariable Long id) {
        InboundOrderDTO order = orderService.getOrderDetail(id);
        return ResponseEntity.ok(order);
    }

    /**
     * 创建入库单
     */
    @PostMapping
    public ResponseEntity<InboundOrderDTO> createOrder(@Valid @RequestBody InboundOrderCreateRequest request) {
        InboundOrderDTO order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    /**
     * 更新入库单
     */
    @PutMapping("/{id}")
    public ResponseEntity<InboundOrderDTO> updateOrder(
            @PathVariable Long id, 
            @Valid @RequestBody InboundOrderCreateRequest request) {
        InboundOrderDTO order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(order);
    }

    /**
     * 删除入库单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 确认收货
     */
    @PostMapping("/{id}/confirm-receipt")
    public ResponseEntity<Void> confirmReceipt(
            @PathVariable Long id, 
            @Valid @RequestBody ConfirmReceiptRequest request) {
        orderService.confirmReceipt(id, request.getItems());
        return ResponseEntity.ok().build();
    }

    /**
     * 强制完成入库单
     */
    @PostMapping("/{id}/force-complete")
    public ResponseEntity<Void> forceCompleteOrder(@PathVariable Long id) {
        orderService.forceCompleteOrder(id);
        return ResponseEntity.ok().build();
    }
}

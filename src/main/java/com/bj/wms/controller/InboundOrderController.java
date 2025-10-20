package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.InboundOrderService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<PageResult<InboundOrderDTO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        InboundOrderQueryRequest request = new InboundOrderQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setOrderNo(orderNo);
        request.setWarehouseId(warehouseId);
        request.setSupplierId(supplierId);
        request.setStatus(status);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        
        PageResult<InboundOrderDTO> result = orderService.getOrderList(request);
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
}

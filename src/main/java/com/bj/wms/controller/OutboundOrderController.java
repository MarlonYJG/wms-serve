package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.OutboundOrderService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 出库单控制器
 */
@Slf4j
@RestController
@RequestMapping("/outbound-order")
@RequiredArgsConstructor
public class OutboundOrderController {

    private final OutboundOrderService outboundOrderService;

    /**
     * 分页查询出库单列表
     */
    @GetMapping
    public ResponseEntity<PageResult<OutboundOrderDTO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        OutboundOrderQueryRequest request = new OutboundOrderQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setOrderNo(orderNo);
        request.setWarehouseId(warehouseId);
        request.setCustomerId(customerId);
        request.setStatus(status);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        
        PageResult<OutboundOrderDTO> result = outboundOrderService.getOrderList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取出库单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<OutboundOrderDTO> getOrderDetail(@PathVariable Long id) {
        OutboundOrderDTO order = outboundOrderService.getOrderDetail(id);
        return ResponseEntity.ok(order);
    }

    /**
     * 创建出库单
     */
    @PostMapping
    public ResponseEntity<OutboundOrderDTO> createOrder(@Valid @RequestBody OutboundOrderCreateRequest request) {
        OutboundOrderDTO order = outboundOrderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    /**
     * 更新出库单
     */
    @PutMapping("/{id}")
    public ResponseEntity<OutboundOrderDTO> updateOrder(
            @PathVariable Long id, 
            @Valid @RequestBody OutboundOrderCreateRequest request) {
        OutboundOrderDTO order = outboundOrderService.updateOrder(id, request);
        return ResponseEntity.ok(order);
    }

    /**
     * 删除出库单
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        outboundOrderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 分配库存
     */
    @PostMapping("/{id}/allocate")
    public ResponseEntity<Void> allocateInventory(@PathVariable Long id) {
        outboundOrderService.allocateInventory(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 生成拣货任务
     */
    @PostMapping("/{id}/generate-picking-tasks")
    public ResponseEntity<Void> generatePickingTasks(@PathVariable Long id) {
        outboundOrderService.generatePickingTasks(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取拣货任务列表
     */
    @GetMapping("/{id}/picking-tasks")
    public ResponseEntity<java.util.List<PickingTaskDTO>> getPickingTasks(@PathVariable Long id) {
        java.util.List<PickingTaskDTO> tasks = outboundOrderService.getPickingTasks(id);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 确认发货
     */
    @PostMapping("/{id}/ship")
    public ResponseEntity<Void> confirmShipment(
            @PathVariable Long id, 
            @RequestBody(required = false) ShipmentRequest request) {
        String trackingNumber = request != null ? request.getTrackingNumber() : null;
        outboundOrderService.confirmShipment(id, trackingNumber);
        return ResponseEntity.ok().build();
    }

    /**
     * 发货请求
     */
    @lombok.Data
    public static class ShipmentRequest {
        private String trackingNumber;
    }
}

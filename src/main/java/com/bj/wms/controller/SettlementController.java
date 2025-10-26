package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.SettlementService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 结算单控制器
 */
@Slf4j
@RestController
@RequestMapping("/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 分页查询结算单
     */
    @GetMapping
    public ResponseEntity<PageResult<SettlementDTO>> getSettlementList(SettlementQueryRequest request) {
        PageResult<SettlementDTO> result = settlementService.getSettlementList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取结算单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SettlementDTO> getSettlementById(@PathVariable Long id) {
        SettlementDTO result = settlementService.getSettlementById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建结算单
     */
    @PostMapping
    public ResponseEntity<SettlementDTO> createSettlement(@Valid @RequestBody SettlementCreateRequest request) {
        SettlementDTO result = settlementService.createSettlement(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 添加出库单到结算单
     */
    @PostMapping("/{id}/orders")
    public ResponseEntity<Void> addOutboundOrdersToSettlement(
            @PathVariable Long id,
            @RequestBody List<Long> outboundOrderIds) {
        settlementService.addOutboundOrdersToSettlement(id, outboundOrderIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 从结算单移除出库单
     */
    @DeleteMapping("/{id}/orders/{orderId}")
    public ResponseEntity<Void> removeOutboundOrderFromSettlement(
            @PathVariable Long id,
            @PathVariable Long orderId) {
        settlementService.removeOutboundOrderFromSettlement(id, orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 提交结算单
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitSettlement(@PathVariable Long id) {
        settlementService.submitSettlement(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 审核结算单
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveSettlement(@PathVariable Long id) {
        settlementService.approveSettlement(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 结清结算单
     */
    @PostMapping("/{id}/close")
    public ResponseEntity<Void> closeSettlement(@PathVariable Long id) {
        settlementService.closeSettlement(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 作废结算单
     */
    @PostMapping("/{id}/void")
    public ResponseEntity<Void> voidSettlement(@PathVariable Long id) {
        settlementService.voidSettlement(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取可结算的出库单列表
     */
    @GetMapping("/settlable-orders")
    public ResponseEntity<List<OutboundOrderDTO>> getSettlableOutboundOrders(@RequestParam Long customerId) {
        List<OutboundOrderDTO> result = settlementService.getSettlableOutboundOrders(customerId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有可结算的出库单列表（支持分页和搜索）
     */
    @GetMapping("/all-settlable-orders")
    public ResponseEntity<PageResult<OutboundOrderDTO>> getAllSettlableOutboundOrders(OutboundOrderQueryRequest request) {
        PageResult<OutboundOrderDTO> result = settlementService.getAllSettlableOutboundOrders(request);
        return ResponseEntity.ok(result);
    }
}

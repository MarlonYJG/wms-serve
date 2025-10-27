package com.bj.wms.controller;

import com.bj.wms.dto.OutboundChargeCreateRequest;
import com.bj.wms.dto.OutboundChargeDTO;
import com.bj.wms.service.OutboundChargeService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 出库费用控制器
 */
@Slf4j
@RestController
@RequestMapping("/outbound-charges")
@RequiredArgsConstructor
public class OutboundChargeController {

    private final OutboundChargeService outboundChargeService;

    /**
     * 分页查询出库费用
     */
    @GetMapping
    public ResponseEntity<PageResult<OutboundChargeDTO>> getChargeList(
            @RequestParam(required = false) Long outboundOrderId,
            @RequestParam(required = false) String outboundOrderNo,
            @RequestParam(required = false) Long chargeType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        // 安全解析时间参数
        if (startTime != null && !startTime.trim().isEmpty()) {
            try {
                start = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.warn("Invalid startTime format: {}", startTime);
            }
        }
        
        if (endTime != null && !endTime.trim().isEmpty()) {
            try {
                end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.warn("Invalid endTime format: {}", endTime);
            }
        }
        
        Page<OutboundChargeDTO> pageResult = outboundChargeService.getChargeList(
            outboundOrderId, outboundOrderNo, chargeType, start, end, page, size);
        
        PageResult<OutboundChargeDTO> result = new PageResult<>(
            pageResult.getContent(), 
            pageResult.getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 根据出库单ID查询费用
     */
    @GetMapping("/outbound-orders/{orderId}")
    public ResponseEntity<List<OutboundChargeDTO>> getChargesByOutboundOrderId(@PathVariable Long orderId) {
        List<OutboundChargeDTO> result = outboundChargeService.getChargesByOutboundOrderId(orderId);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建出库费用
     */
    @PostMapping
    public ResponseEntity<OutboundChargeDTO> createCharge(@Valid @RequestBody OutboundChargeCreateRequest request) {
        OutboundChargeDTO result = outboundChargeService.createCharge(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新出库费用
     */
    @PutMapping("/{id}")
    public ResponseEntity<OutboundChargeDTO> updateCharge(
            @PathVariable Long id,
            @Valid @RequestBody OutboundChargeCreateRequest request) {
        OutboundChargeDTO result = outboundChargeService.updateCharge(id, request);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除出库费用
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharge(@PathVariable Long id) {
        outboundChargeService.deleteCharge(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量创建出库费用
     */
    @PostMapping("/outbound-orders/{orderId}/batch")
    public ResponseEntity<List<OutboundChargeDTO>> batchCreateCharges(
            @PathVariable Long orderId,
            @Valid @RequestBody List<OutboundChargeCreateRequest> requests) {
        List<OutboundChargeDTO> result = outboundChargeService.batchCreateCharges(orderId, requests);
        return ResponseEntity.ok(result);
    }
}

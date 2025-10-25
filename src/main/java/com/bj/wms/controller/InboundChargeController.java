package com.bj.wms.controller;

import com.bj.wms.dto.InboundChargeCreateRequest;
import com.bj.wms.dto.InboundChargeDTO;
import com.bj.wms.service.InboundChargeService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库费用控制器
 */
@Slf4j
@RestController
@RequestMapping("/inbound-charge")
@RequiredArgsConstructor
public class InboundChargeController {

    private final InboundChargeService inboundChargeService;

    /**
     * 分页查询入库费用
     */
    @GetMapping
    public ResponseEntity<PageResult<InboundChargeDTO>> getChargeList(
            @RequestParam(required = false) Long inboundOrderId,
            @RequestParam(required = false) String inboundOrderNo,
            @RequestParam(required = false) Long chargeType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime) : null;

        Page<InboundChargeDTO> pageResult = inboundChargeService.getChargeList(
            inboundOrderId, inboundOrderNo, chargeType, start, end, page, size);

        PageResult<InboundChargeDTO> result = new PageResult<>(
            pageResult.getContent(),
            pageResult.getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 根据入库单ID查询费用
     */
    @GetMapping("/order/{inboundOrderId}")
    public ResponseEntity<List<InboundChargeDTO>> getChargesByOrderId(@PathVariable Long inboundOrderId) {
        List<InboundChargeDTO> charges = inboundChargeService.getChargesByOrderId(inboundOrderId);
        return ResponseEntity.ok(charges);
    }

    /**
     * 创建入库费用
     */
    @PostMapping
    public ResponseEntity<InboundChargeDTO> createCharge(@Valid @RequestBody InboundChargeCreateRequest request) {
        InboundChargeDTO charge = inboundChargeService.createCharge(request);
        return ResponseEntity.ok(charge);
    }

    /**
     * 更新入库费用
     */
    @PutMapping("/{id}")
    public ResponseEntity<InboundChargeDTO> updateCharge(
            @PathVariable Long id,
            @Valid @RequestBody InboundChargeCreateRequest request) {
        InboundChargeDTO charge = inboundChargeService.updateCharge(id, request);
        return ResponseEntity.ok(charge);
    }

    /**
     * 删除入库费用
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharge(@PathVariable Long id) {
        inboundChargeService.deleteCharge(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量创建入库费用
     */
    @PostMapping("/batch")
    public ResponseEntity<List<InboundChargeDTO>> batchCreateCharges(
            @RequestParam Long inboundOrderId,
            @Valid @RequestBody List<InboundChargeCreateRequest> requests) {
        List<InboundChargeDTO> charges = inboundChargeService.batchCreateCharges(inboundOrderId, requests);
        return ResponseEntity.ok(charges);
    }
}

package com.bj.wms.controller;

import com.bj.wms.dto.ChargeDictCreateRequest;
import com.bj.wms.dto.ChargeDictDTO;
import com.bj.wms.service.ChargeDictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 费用字典控制器
 */
@Slf4j
@RestController
@RequestMapping("/charge-dict")
@RequiredArgsConstructor
public class ChargeDictController {

    private final ChargeDictService chargeDictService;

    /**
     * 获取所有费用字典
     */
    @GetMapping
    public ResponseEntity<List<ChargeDictDTO>> getAllChargeDicts() {
        List<ChargeDictDTO> result = chargeDictService.getAllChargeDicts();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取启用的费用字典
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<ChargeDictDTO>> getEnabledChargeDicts() {
        List<ChargeDictDTO> result = chargeDictService.getEnabledChargeDicts();
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索费用字典
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChargeDictDTO>> searchChargeDicts(
            @RequestParam(required = false) String chargeName,
            @RequestParam(required = false) Boolean isEnabled) {
        List<ChargeDictDTO> result = chargeDictService.searchChargeDicts(chargeName, isEnabled);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取费用字典
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChargeDictDTO> getChargeDictById(@PathVariable Long id) {
        ChargeDictDTO result = chargeDictService.getChargeDictById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据费用编码获取费用字典
     */
    @GetMapping("/code/{chargeCode}")
    public ResponseEntity<ChargeDictDTO> getChargeDictByCode(@PathVariable String chargeCode) {
        ChargeDictDTO result = chargeDictService.getChargeDictByCode(chargeCode);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建费用字典
     */
    @PostMapping
    public ResponseEntity<ChargeDictDTO> createChargeDict(@Valid @RequestBody ChargeDictCreateRequest request) {
        ChargeDictDTO result = chargeDictService.createChargeDict(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新费用字典
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChargeDictDTO> updateChargeDict(
            @PathVariable Long id,
            @Valid @RequestBody ChargeDictCreateRequest request) {
        ChargeDictDTO result = chargeDictService.updateChargeDict(id, request);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除费用字典
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargeDict(@PathVariable Long id) {
        chargeDictService.deleteChargeDict(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 启用/禁用费用字典
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleChargeDictStatus(@PathVariable Long id) {
        chargeDictService.toggleChargeDictStatus(id);
        return ResponseEntity.ok().build();
    }
}

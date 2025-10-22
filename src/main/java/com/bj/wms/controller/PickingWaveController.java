package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.PickingWaveService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 拣货波次控制器
 */
@Slf4j
@RestController
@RequestMapping("/picking-waves")
@RequiredArgsConstructor
public class PickingWaveController {

    private final PickingWaveService pickingWaveService;

    /**
     * 分页查询波次列表
     */
    @GetMapping
    public ResponseEntity<PageResult<PickingWaveDTO>> getWaveList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String waveNo,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        PickingWaveQueryRequest request = new PickingWaveQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setWaveNo(waveNo);
        request.setWarehouseId(warehouseId);
        request.setStatus(status);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        
        PageResult<PickingWaveDTO> result = pickingWaveService.getWaveList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取波次详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<PickingWaveDTO> getWaveDetail(@PathVariable Long id) {
        PickingWaveDTO wave = pickingWaveService.getWaveDetail(id);
        return ResponseEntity.ok(wave);
    }

    /**
     * 创建波次
     */
    @PostMapping
    public ResponseEntity<PickingWaveDTO> createWave(@Valid @RequestBody PickingWaveCreateRequest request) {
        PickingWaveDTO wave = pickingWaveService.createWave(request);
        return ResponseEntity.ok(wave);
    }

    /**
     * 开始执行波次
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startWave(@PathVariable Long id) {
        pickingWaveService.startWave(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 完成波次
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeWave(@PathVariable Long id) {
        pickingWaveService.completeWave(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除波次
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWave(@PathVariable Long id) {
        pickingWaveService.deleteWave(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 自动生成波次
     */
    @PostMapping("/auto-generate")
    public ResponseEntity<PickingWaveDTO> autoGenerateWave(@RequestParam Long warehouseId) {
        PickingWaveDTO wave = pickingWaveService.autoGenerateWave(warehouseId);
        return ResponseEntity.ok(wave);
    }
}

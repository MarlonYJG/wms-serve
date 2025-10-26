package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.PackingTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打包任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/packing-tasks")
@RequiredArgsConstructor
public class PackingTaskController {

    private final PackingTaskService packingTaskService;

    /**
     * 分页查询打包任务
     */
    @GetMapping
    public ResponseEntity<Page<PackingTaskDTO>> getPackingTaskList(PackingTaskQueryRequest request) {
        Page<PackingTaskDTO> result = packingTaskService.getPackingTaskList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取打包任务详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<PackingTaskDTO> getPackingTaskById(@PathVariable Long id) {
        PackingTaskDTO result = packingTaskService.getPackingTaskById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建打包任务
     */
    @PostMapping
    public ResponseEntity<PackingTaskDTO> createPackingTask(@RequestBody PackingTaskCreateRequest request) {
        PackingTaskDTO result = packingTaskService.createPackingTask(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 开始打包任务
     */
    @PatchMapping("/{id}/start")
    public ResponseEntity<PackingTaskDTO> startPackingTask(@PathVariable Long id, 
                                                          @RequestParam Long packerId,
                                                          @RequestParam String packerName) {
        PackingTaskDTO result = packingTaskService.startPackingTask(id, packerId, packerName);
        return ResponseEntity.ok(result);
    }

    /**
     * 完成打包任务
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<PackingTaskDTO> completePackingTask(@PathVariable Long id, 
                                                             @RequestBody PackingTaskCompleteRequest request) {
        PackingTaskDTO result = packingTaskService.completePackingTask(id, request);
        return ResponseEntity.ok(result);
    }

    /**
     * 取消打包任务
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPackingTask(@PathVariable Long id) {
        packingTaskService.cancelPackingTask(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除打包任务
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackingTask(@PathVariable Long id) {
        packingTaskService.deletePackingTask(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据出库单ID获取打包任务列表
     */
    @GetMapping("/order/{outboundOrderId}")
    public ResponseEntity<List<PackingTaskDTO>> getPackingTasksByOrderId(@PathVariable Long outboundOrderId) {
        List<PackingTaskDTO> result = packingTaskService.getPackingTasksByOrderId(outboundOrderId);
        return ResponseEntity.ok(result);
    }
}

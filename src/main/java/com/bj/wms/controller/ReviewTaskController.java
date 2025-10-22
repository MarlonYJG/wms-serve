package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.ReviewTaskService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 复核任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/review-tasks")
@RequiredArgsConstructor
public class ReviewTaskController {

    private final ReviewTaskService reviewTaskService;

    /**
     * 分页查询复核任务列表
     */
    @GetMapping
    public ResponseEntity<PageResult<ReviewTaskDTO>> getTaskList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String taskNo,
            @RequestParam(required = false) String outboundOrderNo,
            @RequestParam(required = false) Long outboundOrderId,
            @RequestParam(required = false) Long productSkuId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        ReviewTaskQueryRequest request = new ReviewTaskQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setTaskNo(taskNo);
        request.setOutboundOrderNo(outboundOrderNo);
        request.setOutboundOrderId(outboundOrderId);
        request.setProductSkuId(productSkuId);
        request.setStatus(status);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        
        PageResult<ReviewTaskDTO> result = reviewTaskService.getTaskList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取复核任务详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewTaskDTO> getTaskDetail(@PathVariable Long id) {
        ReviewTaskDTO task = reviewTaskService.getTaskDetail(id);
        return ResponseEntity.ok(task);
    }

    /**
     * 开始复核
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startReview(@PathVariable Long id) {
        reviewTaskService.startReview(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 完成复核
     */
    @PostMapping("/complete")
    public ResponseEntity<Void> completeReview(@Valid @RequestBody ReviewTaskCompleteRequest request) {
        reviewTaskService.completeReview(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 为出库单创建复核任务
     */
    @PostMapping("/create-for-order/{outboundOrderId}")
    public ResponseEntity<List<ReviewTaskDTO>> createTasksForOrder(@PathVariable Long outboundOrderId) {
        List<ReviewTaskDTO> tasks = reviewTaskService.createTasksForOrder(outboundOrderId);
        return ResponseEntity.ok(tasks);
    }
}

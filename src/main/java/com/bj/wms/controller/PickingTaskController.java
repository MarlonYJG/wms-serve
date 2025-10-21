package com.bj.wms.controller;

import com.bj.wms.service.OutboundOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 拣货任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/picking-task")
@RequiredArgsConstructor
public class PickingTaskController {

    private final OutboundOrderService outboundOrderService;

    /**
     * 完成拣货任务
     */
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Void> completePickingTask(
            @PathVariable Long taskId,
            @Valid @RequestBody CompletePickingRequest request) {
        outboundOrderService.completePickingTask(taskId, request.getPickedQuantity());
        return ResponseEntity.ok().build();
    }

    /**
     * 完成拣货请求
     */
    @Data
    public static class CompletePickingRequest {
        @NotNull(message = "拣货数量不能为空")
        private Integer pickedQuantity;
    }
}

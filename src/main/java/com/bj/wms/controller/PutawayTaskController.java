package com.bj.wms.controller;

import com.bj.wms.dto.PutawayTaskDTO;
import com.bj.wms.entity.PutawayTask;
import com.bj.wms.mapper.PutawayTaskMapper;
import com.bj.wms.service.PutawayTaskService;
import com.bj.wms.util.ResponseUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/putaway-tasks")
@RequiredArgsConstructor
public class PutawayTaskController {

    private final PutawayTaskService putawayTaskService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "inboundOrderId", required = false) Long inboundOrderId,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        Page<PutawayTask> result = putawayTaskService.page(page, size, inboundOrderId, status);
        List<PutawayTaskDTO> content = result.getContent().stream().map(PutawayTaskMapper::toDTO).collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber() + 1, result.getSize(), result.getTotalElements());
    }

    @GetMapping("/by-inbound-order/{inboundOrderId}")
    public ResponseEntity<List<PutawayTaskDTO>> listByInboundOrder(@PathVariable Long inboundOrderId) {
        List<PutawayTaskDTO> list = putawayTaskService.listByInboundOrderId(inboundOrderId).stream()
                .map(PutawayTaskMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/generate")
    public ResponseEntity<List<PutawayTaskDTO>> generate(@RequestBody GenerateBody body) {
        List<PutawayTaskDTO> created = putawayTaskService.generate(body.getInboundOrderItemId(), body.getPutawayStrategy())
                .stream().map(PutawayTaskMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{taskId}/start")
    public ResponseEntity<PutawayTaskDTO> start(@PathVariable Long taskId, @RequestBody(required = false) StartBody body) {
        Integer operatorId = body == null ? null : body.getOperatorId();
        PutawayTaskDTO updated = PutawayTaskMapper.toDTO(putawayTaskService.start(taskId, operatorId));
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<PutawayTaskDTO> complete(@PathVariable Long taskId) {
        PutawayTaskDTO updated = PutawayTaskMapper.toDTO(putawayTaskService.complete(taskId));
        return ResponseEntity.ok(updated);
    }

    @Data
    public static class GenerateBody {
        @NotNull
        private Long inboundOrderItemId;
        private String putawayStrategy;
    }

    @Data
    public static class StartBody {
        private Integer operatorId;
    }
}



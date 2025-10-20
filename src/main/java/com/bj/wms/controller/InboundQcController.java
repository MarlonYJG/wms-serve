package com.bj.wms.controller;

import com.bj.wms.dto.InboundQcDTO;
import com.bj.wms.entity.InboundQc;
import com.bj.wms.mapper.InboundQcMapper;
import com.bj.wms.service.InboundQcService;
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
@RequestMapping("/quality-inspections")
@RequiredArgsConstructor
public class InboundQcController {

    private final InboundQcService inboundQcService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "inboundOrderItemId", required = false) Long inboundOrderItemId,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        Page<InboundQc> result = inboundQcService.page(page, size, inboundOrderItemId, status);
        List<InboundQcDTO> content = result.getContent().stream().map(InboundQcMapper::toDTO).collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber() + 1, result.getSize(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        InboundQc qc = inboundQcService.detail(id).orElseThrow(() -> new IllegalArgumentException("质检记录不存在"));
        return ResponseUtil.success(InboundQcMapper.toDTO(qc));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateBody body) {
        InboundQc created = inboundQcService.create(body.getInboundOrderItemId(), body.getRemark());
        return ResponseUtil.created(InboundQcMapper.toDTO(created));
    }

    @PatchMapping("/{id}/result")
    public ResponseEntity<Map<String, Object>> submitResult(@PathVariable Long id, @RequestBody SubmitResultBody body) {
        InboundQc updated = inboundQcService.submitResult(id, body.getQualifiedQuantity(), body.getUnqualifiedQuantity(), body.getRemark());
        return ResponseUtil.success(InboundQcMapper.toDTO(updated), "提交成功");
    }

    @Data
    public static class CreateBody {
        @NotNull
        private Long inboundOrderItemId;
        private String remark;
    }

    @Data
    public static class SubmitResultBody {
        private Integer qualifiedQuantity;
        private Integer unqualifiedQuantity;
        private String remark;
    }
}



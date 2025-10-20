package com.bj.wms.controller;

import com.bj.wms.dto.*;
import com.bj.wms.service.InboundAppointmentService;
import com.bj.wms.util.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/inbound-appointments")
@RequiredArgsConstructor
public class InboundAppointmentController {

    private final InboundAppointmentService appointmentService;

    /**
     * 分页查询预约单列表
     */
    @GetMapping
    public ResponseEntity<PageResult<InboundAppointmentDTO>> getAppointmentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam(required = false) String appointmentNo) {
        
        InboundAppointmentQueryRequest request = new InboundAppointmentQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setWarehouseId(warehouseId);
        request.setSupplierId(supplierId);
        request.setStatus(status);
        request.setAppointmentDate(appointmentDate != null ? appointmentDate.toString() : null);
        request.setAppointmentNo(appointmentNo);
        
        PageResult<InboundAppointmentDTO> result = appointmentService.getAppointmentList(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取预约单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<InboundAppointmentDTO> getAppointmentDetail(@PathVariable Long id) {
        InboundAppointmentDTO appointment = appointmentService.getAppointmentDetail(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 创建预约单
     */
    @PostMapping
    public ResponseEntity<InboundAppointmentDTO> createAppointment(@Valid @RequestBody InboundAppointmentCreateRequest request) {
        InboundAppointmentDTO appointment = appointmentService.createAppointment(request);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 更新预约单
     */
    @PutMapping("/{id}")
    public ResponseEntity<InboundAppointmentDTO> updateAppointment(
            @PathVariable Long id, 
            @Valid @RequestBody InboundAppointmentCreateRequest request) {
        InboundAppointmentDTO appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 审核预约单
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveAppointment(
            @PathVariable Long id, 
            @Valid @RequestBody AppointmentApprovalRequest request) {
        appointmentService.approveAppointment(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消预约单
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id, 
            @RequestBody(required = false) AppointmentCancelRequest request) {
        String remarks = request != null ? request.getRemarks() : null;
        appointmentService.cancelAppointment(id, remarks);
        return ResponseEntity.ok().build();
    }

    /**
     * 推荐时间窗口
     */
    @GetMapping("/suggest-window")
    public ResponseEntity<List<TimeWindowSuggestionDTO>> suggestTimeWindow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long warehouseId) {
        List<TimeWindowSuggestionDTO> suggestions = appointmentService.suggestTimeWindow(date, warehouseId);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * 检查时间冲突
     */
    @GetMapping("/check-conflict")
    public ResponseEntity<TimeConflictResponse> checkTimeConflict(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String timeStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String timeEnd,
            @RequestParam Long warehouseId,
            @RequestParam(required = false) Long excludeId) {
        
        boolean hasConflict = appointmentService.checkTimeConflict(
            date, 
            java.time.LocalTime.parse(timeStart), 
            java.time.LocalTime.parse(timeEnd), 
            warehouseId, 
            excludeId
        );
        
        TimeConflictResponse response = new TimeConflictResponse();
        response.setHasConflict(hasConflict);
        response.setConflictCount(hasConflict ? 1 : 0); // 简化处理
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量审核预约单
     */
    @PatchMapping("/batch-approve")
    public ResponseEntity<Void> batchApproveAppointments(@Valid @RequestBody BatchApprovalRequest request) {
        for (Long id : request.getIds()) {
            AppointmentApprovalRequest approvalRequest = new AppointmentApprovalRequest();
            approvalRequest.setApproved(request.isApproved());
            approvalRequest.setRemarks(request.getRemarks());
            appointmentService.approveAppointment(id, approvalRequest);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 时间冲突响应DTO
     */
    @lombok.Data
    public static class TimeConflictResponse {
        private boolean hasConflict;
        private int conflictCount;
    }

    /**
     * 批量审核请求DTO
     */
    @lombok.Data
    public static class BatchApprovalRequest {
        private List<Long> ids;
        private boolean approved;
        private String remarks;
    }

    /**
     * 预约取消请求DTO
     */
    @lombok.Data
    public static class AppointmentCancelRequest {
        private String remarks;
    }
}

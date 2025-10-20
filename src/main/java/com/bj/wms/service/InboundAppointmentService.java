package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.AppointmentStatus;
import com.bj.wms.entity.InboundAppointment;
import com.bj.wms.entity.InboundAppointmentItem;
import com.bj.wms.mapper.InboundAppointmentMapper;
import com.bj.wms.repository.InboundAppointmentItemRepository;
import com.bj.wms.repository.InboundAppointmentRepository;
import com.bj.wms.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundAppointmentService {

    private final InboundAppointmentRepository appointmentRepository;
    private final InboundAppointmentItemRepository itemRepository;

    /**
     * 分页查询预约单列表
     */
    public PageResult<InboundAppointmentDTO> getAppointmentList(InboundAppointmentQueryRequest request) {
        Pageable pageable = PageRequest.of(
            request.getPage() - 1, 
            request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Specification<InboundAppointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (request.getWarehouseId() != null) {
                predicates.add(cb.equal(root.get("warehouseId"), request.getWarehouseId()));
            }
            if (request.getSupplierId() != null) {
                predicates.add(cb.equal(root.get("supplierId"), request.getSupplierId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), AppointmentStatus.fromCode(request.getStatus())));
            }
            if (StringUtils.hasText(request.getAppointmentDate())) {
                predicates.add(cb.equal(root.get("appointmentDate"), LocalDate.parse(request.getAppointmentDate())));
            }
            if (StringUtils.hasText(request.getAppointmentNo())) {
                predicates.add(cb.like(root.get("appointmentNo"), "%" + request.getAppointmentNo() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<InboundAppointment> page = appointmentRepository.findAll(spec, pageable);
        
        List<InboundAppointmentDTO> dtoList = page.getContent().stream()
            .map(InboundAppointmentMapper::toDTO)
            .toList();

        return new PageResult<>(dtoList, page.getTotalElements());
    }

    /**
     * 获取预约单详情
     */
    public InboundAppointmentDTO getAppointmentDetail(Long id) {
        InboundAppointment appointment = appointmentRepository.findByIdWithItems(id)
            .orElseThrow(() -> new RuntimeException("预约单不存在"));
        return InboundAppointmentMapper.toDTO(appointment, true);
    }

    /**
     * 创建预约单
     */
    @Transactional
    public InboundAppointmentDTO createAppointment(InboundAppointmentCreateRequest request) {
        // 生成预约单号
        String appointmentNo = generateAppointmentNo();
        
        // 检查时间冲突
        checkTimeConflict(request.getAppointmentDate(), request.getAppointmentTimeStart(), 
            request.getAppointmentTimeEnd(), request.getWarehouseId(), null);
        
        // 创建预约单
        InboundAppointment appointment = InboundAppointmentMapper.toEntity(request);
        appointment.setAppointmentNo(appointmentNo);
        
        // 计算总预估数量
        int totalQuantity = request.getAppointmentItems().stream()
            .mapToInt(InboundAppointmentItemCreateRequest::getExpectedQuantity)
            .sum();
        appointment.setTotalExpectedQuantity(totalQuantity);
        
        appointment = appointmentRepository.save(appointment);
        
        log.info("创建预约单成功，预约单号：{}", appointmentNo);
        return InboundAppointmentMapper.toDTO(appointment);
    }

    /**
     * 更新预约单
     */
    @Transactional
    public InboundAppointmentDTO updateAppointment(Long id, InboundAppointmentCreateRequest request) {
        InboundAppointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("预约单不存在"));
        
        // 只有待审核状态才能修改
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("只有待审核状态的预约单才能修改");
        }
        
        // 检查时间冲突（排除当前预约单）
        checkTimeConflict(request.getAppointmentDate(), request.getAppointmentTimeStart(), 
            request.getAppointmentTimeEnd(), request.getWarehouseId(), id);
        
        // 更新基本信息
        appointment.setWarehouseId(request.getWarehouseId());
        appointment.setSupplierId(request.getSupplierId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTimeStart(request.getAppointmentTimeStart());
        appointment.setAppointmentTimeEnd(request.getAppointmentTimeEnd());
        appointment.setSpecialRequirements(request.getSpecialRequirements());
        
        // 删除原有明细
        itemRepository.deleteByAppointmentId(id);
        
        // 添加新明细
        List<InboundAppointmentItem> items = request.getAppointmentItems().stream()
            .map(itemRequest -> {
                InboundAppointmentItem item = new InboundAppointmentItem();
                item.setAppointmentId(id);
                item.setProductSkuId(itemRequest.getProductSkuId());
                item.setExpectedQuantity(itemRequest.getExpectedQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setBatchNo(itemRequest.getBatchNo());
                item.setProductionDate(itemRequest.getProductionDate());
                item.setExpiryDate(itemRequest.getExpiryDate());
                return item;
            })
            .toList();
        itemRepository.saveAll(items);
        
        // 重新计算总预估数量
        int totalQuantity = request.getAppointmentItems().stream()
            .mapToInt(InboundAppointmentItemCreateRequest::getExpectedQuantity)
            .sum();
        appointment.setTotalExpectedQuantity(totalQuantity);
        
        appointment = appointmentRepository.save(appointment);
        
        log.info("更新预约单成功，预约单号：{}", appointment.getAppointmentNo());
        return InboundAppointmentMapper.toDTO(appointment);
    }

    /**
     * 审核预约单
     */
    @Transactional
    public void approveAppointment(Long id, AppointmentApprovalRequest request) {
        InboundAppointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("预约单不存在"));
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("只有待审核状态的预约单才能审核");
        }
        
        if (request.isApproved()) {
            appointment.setStatus(AppointmentStatus.APPROVED);
            appointment.setApprovedTime(java.time.LocalDateTime.now());
            // TODO: 设置审核人ID
            // appointment.setApprovedBy(getCurrentUserId());
        } else {
            appointment.setStatus(AppointmentStatus.REJECTED);
        }
        
        if (StringUtils.hasText(request.getRemarks())) {
            appointment.setRemark(request.getRemarks());
        }
        
        appointmentRepository.save(appointment);
        
        log.info("审核预约单成功，预约单号：{}，审核结果：{}", 
            appointment.getAppointmentNo(), request.isApproved() ? "通过" : "拒绝");
    }

    /**
     * 取消预约单
     */
    @Transactional
    public void cancelAppointment(Long id, String remarks) {
        InboundAppointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("预约单不存在"));
        
        if (appointment.getStatus() == AppointmentStatus.CANCELED) {
            throw new RuntimeException("预约单已经取消");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("已完成的预约单不能取消");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELED);
        if (StringUtils.hasText(remarks)) {
            appointment.setRemark(remarks);
        }
        
        appointmentRepository.save(appointment);
        
        log.info("取消预约单成功，预约单号：{}", appointment.getAppointmentNo());
    }

    /**
     * 推荐时间窗口
     */
    public List<TimeWindowSuggestionDTO> suggestTimeWindow(LocalDate date, Long warehouseId) {
        List<TimeWindowSuggestionDTO> suggestions = new ArrayList<>();
        
        // 生成时间窗口（8:00-20:00，每2小时一个窗口）
        for (int hour = 8; hour < 20; hour += 2) {
            LocalTime startTime = LocalTime.of(hour, 0);
            LocalTime endTime = LocalTime.of(hour + 2, 0);
            
            // 检查该时间段的预约数量
            long appointmentCount = appointmentRepository.countByAppointmentDateAndAppointmentTimeStartBetweenAndWarehouseIdAndStatus(
                date, startTime, endTime, warehouseId, AppointmentStatus.APPROVED);
            
            TimeWindowSuggestionDTO suggestion = new TimeWindowSuggestionDTO();
            suggestion.setTimeStart(startTime.toString());
            suggestion.setTimeEnd(endTime.toString());
            suggestion.setCapacity(10); // 假设每个时间段容量为10
            suggestion.setAvailable((int) (10 - appointmentCount));
            suggestion.setIsRecommended(appointmentCount < 5); // 预约数少于5个时推荐
            
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }

    /**
     * 检查时间冲突
     */
    public boolean checkTimeConflict(LocalDate date, LocalTime startTime, LocalTime endTime, 
                                   Long warehouseId, Long excludeId) {
        long count = appointmentRepository.countByAppointmentDateAndAppointmentTimeStartBetweenAndWarehouseIdAndStatus(
            date, startTime, endTime, warehouseId, AppointmentStatus.APPROVED);
        
        if (excludeId != null) {
            // 排除当前预约单
            InboundAppointment excludeAppointment = appointmentRepository.findById(excludeId).orElse(null);
            if (excludeAppointment != null && excludeAppointment.getStatus() == AppointmentStatus.APPROVED) {
                count--;
            }
        }
        
        return count > 0;
    }

    /**
     * 生成预约单号
     */
    private String generateAppointmentNo() {
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "APT" + dateStr + uuid;
    }
}

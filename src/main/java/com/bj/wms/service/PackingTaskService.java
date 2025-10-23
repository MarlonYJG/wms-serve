package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.OutboundOrder;
import com.bj.wms.entity.PackingTask;
import com.bj.wms.entity.PackingMaterial;
import com.bj.wms.mapper.PackingTaskMapper;
import com.bj.wms.repository.OutboundOrderRepository;
import com.bj.wms.repository.PackingTaskRepository;
import com.bj.wms.repository.PackingMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 打包任务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PackingTaskService {

    private final PackingTaskRepository packingTaskRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final PackingMaterialRepository packingMaterialRepository;
    private final PackingTaskMapper packingTaskMapper;

    /**
     * 分页查询打包任务
     */
    public Page<PackingTaskDTO> getPackingTaskList(PackingTaskQueryRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Specification<PackingTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTaskNo() != null && !request.getTaskNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("taskNo"), "%" + request.getTaskNo() + "%"));
            }
            if (request.getOutboundOrderId() != null) {
                predicates.add(cb.equal(root.get("outboundOrderId"), request.getOutboundOrderId()));
            }
            if (request.getPackingMaterialId() != null) {
                predicates.add(cb.equal(root.get("packingMaterialId"), request.getPackingMaterialId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getPackerId() != null) {
                predicates.add(cb.equal(root.get("packerId"), request.getPackerId()));
            }
            if (request.getPackerName() != null && !request.getPackerName().trim().isEmpty()) {
                predicates.add(cb.like(root.get("packerName"), "%" + request.getPackerName() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<PackingTask> page = packingTaskRepository.findAll(spec, pageable);
        return page.map(packingTaskMapper::toDTO);
    }

    /**
     * 根据ID获取打包任务详情
     */
    public PackingTaskDTO getPackingTaskById(Long id) {
        PackingTask task = packingTaskRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("打包任务不存在"));
        return packingTaskMapper.toDTO(task);
    }

    /**
     * 创建打包任务
     */
    @Transactional
    public PackingTaskDTO createPackingTask(PackingTaskCreateRequest request) {
        // 验证出库单是否存在且状态正确
        OutboundOrder order = outboundOrderRepository.findById(request.getOutboundOrderId())
                .orElseThrow(() -> new RuntimeException("出库单不存在"));

        if (order.getStatus() != 3) { // 只有拣货中状态的订单才能创建打包任务
            throw new RuntimeException("只有拣货中状态的出库单才能创建打包任务");
        }

        // 验证包装材料是否存在
        if (request.getPackingMaterialId() != null) {
            PackingMaterial material = packingMaterialRepository.findById(request.getPackingMaterialId())
                    .orElseThrow(() -> new RuntimeException("包装材料不存在"));
            if (!material.getIsEnabled()) {
                throw new RuntimeException("包装材料已禁用");
            }
        }

        // 检查是否已存在打包任务
        List<PackingTask> existingTasks = packingTaskRepository.findByOutboundOrderIdAndStatus(
                request.getOutboundOrderId(), 1);
        if (!existingTasks.isEmpty()) {
            throw new RuntimeException("该出库单已存在待打包任务");
        }

        // 创建打包任务
        PackingTask task = new PackingTask();
        task.setTaskNo(generateTaskNo());
        task.setOutboundOrderId(request.getOutboundOrderId());
        task.setPackingMaterialId(request.getPackingMaterialId());
        task.setStatus(1); // 待打包
        task.setRemark(request.getRemark());
        task.setCreatedTime(LocalDateTime.now());

        task = packingTaskRepository.save(task);
        return packingTaskMapper.toDTO(task);
    }

    /**
     * 开始打包任务
     */
    @Transactional
    public PackingTaskDTO startPackingTask(Long id, Long packerId, String packerName) {
        PackingTask task = packingTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("打包任务不存在"));

        if (task.getStatus() != 1) {
            throw new RuntimeException("只有待打包状态的任务才能开始打包");
        }

        task.setStatus(2); // 打包中
        task.setPackerId(packerId);
        task.setPackerName(packerName);
        task.setUpdatedTime(LocalDateTime.now());

        task = packingTaskRepository.save(task);
        return packingTaskMapper.toDTO(task);
    }

    /**
     * 完成打包任务
     */
    @Transactional
    public PackingTaskDTO completePackingTask(Long id, PackingTaskCompleteRequest request) {
        PackingTask task = packingTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("打包任务不存在"));

        if (task.getStatus() != 2) {
            throw new RuntimeException("只有打包中状态的任务才能完成打包");
        }

        task.setStatus(3); // 已完成
        task.setWeight(request.getWeight());
        task.setVolume(request.getVolume());
        task.setDimensions(request.getDimensions());
        task.setPackerId(request.getPackerId());
        task.setPackerName(request.getPackerName());
        task.setPackedTime(LocalDateTime.now());
        task.setRemark(request.getRemark());
        task.setUpdatedTime(LocalDateTime.now());

        task = packingTaskRepository.save(task);

        // 更新出库单状态为已发货
        OutboundOrder order = outboundOrderRepository.findById(task.getOutboundOrderId())
                .orElseThrow(() -> new RuntimeException("出库单不存在"));
        order.setStatus(4); // 已发货
        order.setUpdatedTime(LocalDateTime.now());
        outboundOrderRepository.save(order);

        return packingTaskMapper.toDTO(task);
    }

    /**
     * 取消打包任务
     */
    @Transactional
    public void cancelPackingTask(Long id) {
        PackingTask task = packingTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("打包任务不存在"));

        if (task.getStatus() == 3) {
            throw new RuntimeException("已完成的任务不能取消");
        }

        task.setStatus(4); // 已取消
        task.setUpdatedTime(LocalDateTime.now());
        packingTaskRepository.save(task);
    }

    /**
     * 删除打包任务
     */
    @Transactional
    public void deletePackingTask(Long id) {
        PackingTask task = packingTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("打包任务不存在"));

        if (task.getStatus() != 1) {
            throw new RuntimeException("只有待打包状态的任务才能删除");
        }

        packingTaskRepository.deleteById(id);
    }

    /**
     * 根据出库单ID获取打包任务列表
     */
    public List<PackingTaskDTO> getPackingTasksByOrderId(Long outboundOrderId) {
        List<PackingTask> tasks = packingTaskRepository.findByOutboundOrderId(outboundOrderId);
        return packingTaskMapper.toDTOList(tasks);
    }

    /**
     * 生成任务编号
     */
    private String generateTaskNo() {
        String prefix = "PK" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查找当天最大的任务编号
        List<PackingTask> todayTasks = packingTaskRepository.findAll().stream()
                .filter(task -> task.getTaskNo().startsWith(prefix))
                .sorted((t1, t2) -> t2.getTaskNo().compareTo(t1.getTaskNo()))
                .toList();
        
        String maxNo = todayTasks.stream()
                .findFirst()
                .map(task -> task.getTaskNo().substring(prefix.length()))
                .orElse("0000");
        
        int nextNo = Integer.parseInt(maxNo) + 1;
        return prefix + String.format("%04d", nextNo);
    }
}

package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.*;
import com.bj.wms.mapper.PickingTaskMapper;
import com.bj.wms.repository.*;
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

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 拣货任务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PickingTaskService {

    private final PickingTaskRepository pickingTaskRepository;
    private final PickingWaveRepository pickingWaveRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * 分页查询拣货任务列表
     */
    @Transactional(readOnly = true)
    public PageResult<PickingTaskDTO> getTaskList(PickingTaskQueryRequest request) {
        // 构建查询条件
        Specification<PickingTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTaskNo() != null && !request.getTaskNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("taskNo"), "%" + request.getTaskNo() + "%"));
            }
            if (request.getWaveNo() != null && !request.getWaveNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("waveNo"), "%" + request.getWaveNo() + "%"));
            }
            if (request.getOutboundOrderId() != null) {
                predicates.add(cb.equal(root.get("outboundOrderId"), request.getOutboundOrderId()));
            }
            if (request.getProductSkuId() != null) {
                predicates.add(cb.equal(root.get("productSkuId"), request.getProductSkuId()));
            }
            if (request.getFromLocationId() != null) {
                predicates.add(cb.equal(root.get("fromLocationId"), request.getFromLocationId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getStartTime() != null && !request.getStartTime().trim().isEmpty()) {
                LocalDateTime startTime = LocalDateTime.parse(request.getStartTime() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), startTime));
            }
            if (request.getEndTime() != null && !request.getEndTime().trim().isEmpty()) {
                LocalDateTime endTime = LocalDateTime.parse(request.getEndTime() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), endTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        Pageable pageable = PageRequest.of(
            request.getPage() - 1, 
            request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Page<PickingTask> page = pickingTaskRepository.findAll(spec, pageable);

        // 转换为DTO
        List<PickingTaskDTO> content = page.getContent().stream()
            .map(PickingTaskMapper::toDTO)
            .collect(Collectors.toList());

        return new PageResult<>(content, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    /**
     * 获取拣货任务详情
     */
    @Transactional(readOnly = true)
    public PickingTaskDTO getTaskDetail(Long id) {
        PickingTask task = pickingTaskRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new RuntimeException("拣货任务不存在"));
        return PickingTaskMapper.toDTO(task);
    }

    /**
     * 开始拣货
     */
    @Transactional
    public void startPicking(Long taskId) {
        PickingTask task = pickingTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("拣货任务不存在"));

        if (task.getStatus() != 1) {
            throw new RuntimeException("只有待拣选状态的任务才能开始拣货");
        }

        // 更新状态为部分完成
        task.setStatus(2);
        task.setUpdatedTime(LocalDateTime.now());
        pickingTaskRepository.save(task);

        log.info("拣货任务 {} 开始执行", task.getTaskNo());
    }

    /**
     * 完成拣货
     */
    @Transactional
    public void completePicking(PickingTaskCompleteRequest request) {
        PickingTask task = pickingTaskRepository.findByIdWithDetails(request.getTaskId())
            .orElseThrow(() -> new RuntimeException("拣货任务不存在"));

        if (task.getStatus() == 3) {
            throw new RuntimeException("任务已完成，无法重复操作");
        }

        // 验证拣货数量
        if (request.getPickedQuantity() <= 0) {
            throw new RuntimeException("拣货数量必须大于0");
        }

        if (request.getPickedQuantity() > task.getQuantity()) {
            throw new RuntimeException("拣货数量不能超过任务数量");
        }

        // 更新拣货数量
        int newPickedQuantity = task.getPickedQuantity() + request.getPickedQuantity();
        task.setPickedQuantity(newPickedQuantity);

        // 判断任务状态
        if (newPickedQuantity >= task.getQuantity()) {
            task.setStatus(3); // 已完成
            log.info("拣货任务 {} 已完成", task.getTaskNo());
        } else {
            task.setStatus(2); // 部分完成
            log.info("拣货任务 {} 部分完成，已拣数量：{}", task.getTaskNo(), newPickedQuantity);
        }

        task.setUpdatedTime(LocalDateTime.now());
        pickingTaskRepository.save(task);

        // 更新库存
        updateInventory(task, request.getPickedQuantity());

        // 更新波次完成情况
        updateWaveProgress(task);
    }

    /**
     * 更新库存
     */
    private void updateInventory(PickingTask task, Integer pickedQuantity) {
        // 查找对应的库存记录
        List<Inventory> inventories = inventoryRepository.findByWarehouseIdAndLocationIdAndProductSkuId(
            task.getOutboundOrder().getWarehouseId(),
            task.getFromLocationId(),
            task.getProductSkuId()
        );

        if (inventories.isEmpty()) {
            throw new RuntimeException("未找到对应的库存记录");
        }

        Inventory inventory = inventories.get(0);
        
        // 检查库存是否足够
        if (inventory.getQuantity() < pickedQuantity) {
            throw new RuntimeException("库存不足，无法完成拣货");
        }

        // 更新库存数量
        inventory.setQuantity(inventory.getQuantity() - pickedQuantity);
        inventory.setUpdatedTime(LocalDateTime.now());
        inventoryRepository.save(inventory);

        log.info("库存更新：库位 {} 商品 {} 减少数量 {}", 
            task.getFromLocation().getLocationCode(),
            task.getProductSku().getSkuName(),
            pickedQuantity);
    }

    /**
     * 更新波次进度
     */
    private void updateWaveProgress(PickingTask task) {
        if (task.getPickingWaveId() == null) {
            return;
        }

        PickingWave wave = pickingWaveRepository.findById(task.getPickingWaveId())
            .orElse(null);

        if (wave == null) {
            return;
        }

        // 统计该波次下的任务完成情况
        List<PickingTask> waveTasks = pickingTaskRepository.findByWaveNo(wave.getWaveNo());
        long completedTasks = waveTasks.stream()
            .filter(t -> t.getStatus() == 3)
            .count();

        wave.setCompletedTaskCount((int) completedTasks);
        wave.setUpdatedTime(LocalDateTime.now());
        pickingWaveRepository.save(wave);

        log.info("波次 {} 进度更新：已完成任务 {}/{}", 
            wave.getWaveNo(), completedTasks, waveTasks.size());
    }

    /**
     * 批量创建拣货任务
     */
    @Transactional
    public List<PickingTaskDTO> createTasksForOrder(Long outboundOrderId) {
        OutboundOrder order = outboundOrderRepository.findByIdWithItems(outboundOrderId)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        if (order.getStatus() != 2) {
            throw new RuntimeException("只有已分配库存的出库单才能创建拣货任务");
        }

        List<PickingTask> tasks = new ArrayList<>();

        for (OutboundOrderItem item : order.getItems()) {
            // 查找可用的库存位置
            List<Inventory> inventories = inventoryRepository.findByWarehouseIdAndProductSkuIdAndQuantityGreaterThan(
                order.getWarehouseId(),
                item.getProductSkuId(),
                0
            );

            for (Inventory inventory : inventories) {
                if (item.getAllocatedQuantity() <= 0) {
                    break;
                }

                int taskQuantity = Math.min(item.getAllocatedQuantity(), inventory.getQuantity());
                
                PickingTask task = new PickingTask();
                task.setTaskNo(generateTaskNo());
                task.setOutboundOrderId(outboundOrderId);
                task.setProductSkuId(item.getProductSkuId());
                task.setFromLocationId(inventory.getLocationId());
                task.setQuantity(taskQuantity);
                task.setStatus(1);
                task.setPickedQuantity(0);
                task.setCreatedTime(LocalDateTime.now());

                tasks.add(task);
                item.setAllocatedQuantity(item.getAllocatedQuantity() - taskQuantity);
            }
        }

        List<PickingTask> savedTasks = pickingTaskRepository.saveAll(tasks);
        
        return savedTasks.stream()
            .map(PickingTaskMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 生成任务编号
     */
    private String generateTaskNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return "PICK" + dateStr + timeStr;
    }
}

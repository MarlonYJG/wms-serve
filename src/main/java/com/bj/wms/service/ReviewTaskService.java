package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.*;
import com.bj.wms.mapper.ReviewTaskMapper;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 复核任务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewTaskService {

    private final ReviewTaskRepository reviewTaskRepository;
    private final PickingTaskRepository pickingTaskRepository;
    private final OutboundOrderRepository outboundOrderRepository;

    /**
     * 分页查询复核任务列表
     */
    @Transactional(readOnly = true)
    public PageResult<ReviewTaskDTO> getTaskList(ReviewTaskQueryRequest request) {
        // 构建查询条件
        Specification<ReviewTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTaskNo() != null && !request.getTaskNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("taskNo"), "%" + request.getTaskNo() + "%"));
            }
            if (request.getOutboundOrderNo() != null && !request.getOutboundOrderNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("outboundOrder").get("orderNo"), "%" + request.getOutboundOrderNo() + "%"));
            }
            if (request.getOutboundOrderId() != null) {
                predicates.add(cb.equal(root.get("outboundOrderId"), request.getOutboundOrderId()));
            }
            if (request.getProductSkuId() != null) {
                predicates.add(cb.equal(root.get("productSkuId"), request.getProductSkuId()));
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

        Page<ReviewTask> page = reviewTaskRepository.findAll(spec, pageable);

        // 转换为DTO
        List<ReviewTaskDTO> content = page.getContent().stream()
            .map(ReviewTaskMapper::toDTO)
            .collect(Collectors.toList());

        return new PageResult<>(content, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    /**
     * 获取复核任务详情
     */
    @Transactional(readOnly = true)
    public ReviewTaskDTO getTaskDetail(Long id) {
        ReviewTask task = reviewTaskRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new RuntimeException("复核任务不存在"));
        return ReviewTaskMapper.toDTO(task);
    }

    /**
     * 开始复核
     */
    @Transactional
    public void startReview(Long taskId) {
        ReviewTask task = reviewTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("复核任务不存在"));

        if (task.getStatus() != 1) {
            throw new RuntimeException("只有待复核状态的任务才能开始复核");
        }

        // 更新状态为复核中
        task.setStatus(2);
        task.setUpdatedTime(LocalDateTime.now());
        reviewTaskRepository.save(task);

        log.info("复核任务 {} 开始执行", task.getTaskNo());
    }

    /**
     * 完成复核
     */
    @Transactional
    public void completeReview(ReviewTaskCompleteRequest request) {
        ReviewTask task = reviewTaskRepository.findByIdWithDetails(request.getTaskId())
            .orElseThrow(() -> new RuntimeException("复核任务不存在"));

        if (task.getStatus() == 3) {
            throw new RuntimeException("任务已完成，无法重复操作");
        }

        // 验证实际数量
        if (request.getActualQuantity() < 0) {
            throw new RuntimeException("实际数量不能小于0");
        }

        // 更新实际数量和状态
        task.setActualQuantity(request.getActualQuantity());
        task.setRemark(request.getRemark());
        task.setReviewTime(LocalDateTime.now());
        task.setUpdatedTime(LocalDateTime.now());

        // 判断复核结果
        if (request.getActualQuantity().equals(task.getExpectedQuantity())) {
            task.setStatus(3); // 复核完成
            log.info("复核任务 {} 完成，数量一致", task.getTaskNo());
        } else {
            task.setStatus(4); // 复核异常
            log.warn("复核任务 {} 异常，预期数量：{}，实际数量：{}", 
                task.getTaskNo(), task.getExpectedQuantity(), request.getActualQuantity());
        }

        reviewTaskRepository.save(task);

        // 如果复核异常，需要处理异常情况
        if (task.getStatus() == 4) {
            handleReviewException(task);
        }
    }

    /**
     * 处理复核异常
     */
    private void handleReviewException(ReviewTask task) {
        // 这里可以添加异常处理逻辑，比如：
        // 1. 发送通知给相关人员
        // 2. 创建异常处理任务
        // 3. 记录异常日志
        log.warn("复核异常处理：任务 {} 数量不匹配", task.getTaskNo());
    }

    /**
     * 批量创建复核任务
     */
    @Transactional
    public List<ReviewTaskDTO> createTasksForOrder(Long outboundOrderId) {
        OutboundOrder order = outboundOrderRepository.findByIdWithItems(outboundOrderId)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        if (order.getStatus() != 3) {
            throw new RuntimeException("只有已拣货的出库单才能创建复核任务");
        }

        // 查找该出库单下已完成的拣货任务
        List<PickingTask> pickingTasks = pickingTaskRepository.findByOutboundOrderIdAndStatus(outboundOrderId, 3);
        
        if (pickingTasks.isEmpty()) {
            throw new RuntimeException("该出库单下没有已完成的拣货任务");
        }

        List<ReviewTask> tasks = new ArrayList<>();

        for (PickingTask pickingTask : pickingTasks) {
            // 检查是否已存在复核任务
            if (reviewTaskRepository.existsByTaskNo("REV" + pickingTask.getTaskNo())) {
                continue;
            }

            ReviewTask task = new ReviewTask();
            task.setTaskNo(generateTaskNo());
            task.setOutboundOrderId(outboundOrderId);
            task.setProductSkuId(pickingTask.getProductSkuId());
            task.setExpectedQuantity(pickingTask.getPickedQuantity());
            task.setActualQuantity(0);
            task.setStatus(1);
            task.setCreatedTime(LocalDateTime.now());

            tasks.add(task);
        }

        List<ReviewTask> savedTasks = reviewTaskRepository.saveAll(tasks);
        
        return savedTasks.stream()
            .map(ReviewTaskMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 生成任务编号
     */
    private String generateTaskNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        return "REV" + dateStr + timeStr + uuid;
    }
}

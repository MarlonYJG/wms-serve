package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.*;
import com.bj.wms.mapper.PickingWaveMapper;
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
 * 拣货波次服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PickingWaveService {

    private final PickingWaveRepository pickingWaveRepository;
    private final PickingTaskRepository pickingTaskRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * 分页查询波次列表
     */
    @Transactional(readOnly = true)
    public PageResult<PickingWaveDTO> getWaveList(PickingWaveQueryRequest request) {
        // 构建查询条件
        Specification<PickingWave> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getWaveNo() != null && !request.getWaveNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("waveNo"), "%" + request.getWaveNo() + "%"));
            }
            if (request.getWarehouseId() != null) {
                predicates.add(cb.equal(root.get("warehouseId"), request.getWarehouseId()));
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

        Page<PickingWave> page = pickingWaveRepository.findAll(spec, pageable);

        // 转换为DTO
        List<PickingWaveDTO> content = page.getContent().stream()
            .map(PickingWaveMapper::toDTO)
            .collect(Collectors.toList());

        return new PageResult<>(content, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    /**
     * 获取波次详情
     */
    @Transactional(readOnly = true)
    public PickingWaveDTO getWaveDetail(Long id) {
        PickingWave wave = pickingWaveRepository.findByIdWithWarehouse(id)
            .orElseThrow(() -> new RuntimeException("波次不存在"));
        return PickingWaveMapper.toDTO(wave);
    }

    /**
     * 创建波次
     */
    @Transactional
    public PickingWaveDTO createWave(PickingWaveCreateRequest request) {
        // 验证仓库是否存在
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new RuntimeException("仓库不存在"));

        // 检查波次号是否已存在
        if (pickingWaveRepository.existsByWaveNo(request.getWaveNo())) {
            throw new RuntimeException("波次号已存在");
        }

        // 创建波次
        PickingWave wave = new PickingWave();
        wave.setWaveNo(request.getWaveNo());
        wave.setWarehouseId(request.getWarehouseId());
        wave.setStatus(1); // 待执行
        wave.setOrderCount(0);
        wave.setTaskCount(0);
        wave.setCompletedTaskCount(0);
        wave.setRemark(request.getRemark());
        wave.setCreatedTime(LocalDateTime.now());

        wave = pickingWaveRepository.save(wave);

        return PickingWaveMapper.toDTO(wave);
    }

    /**
     * 开始执行波次
     */
    @Transactional
    public void startWave(Long id) {
        PickingWave wave = pickingWaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("波次不存在"));

        if (wave.getStatus() != 1) {
            throw new RuntimeException("只有待执行状态的波次才能开始执行");
        }

        // 更新状态
        wave.setStatus(2); // 执行中
        wave.setStartedTime(LocalDateTime.now());
        wave.setUpdatedTime(LocalDateTime.now());

        pickingWaveRepository.save(wave);
    }

    /**
     * 完成波次
     */
    @Transactional
    public void completeWave(Long id) {
        PickingWave wave = pickingWaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("波次不存在"));

        if (wave.getStatus() != 2) {
            throw new RuntimeException("只有执行中状态的波次才能完成");
        }

        // 检查是否所有任务都已完成
        List<PickingTask> tasks = pickingTaskRepository.findByWaveNo(wave.getWaveNo());
        boolean allCompleted = tasks.stream().allMatch(task -> task.getStatus() == 3);
        
        if (!allCompleted) {
            throw new RuntimeException("还有未完成的任务，无法完成波次");
        }

        // 更新状态
        wave.setStatus(3); // 已完成
        wave.setCompletedTime(LocalDateTime.now());
        wave.setUpdatedTime(LocalDateTime.now());

        pickingWaveRepository.save(wave);
    }

    /**
     * 删除波次
     */
    @Transactional
    public void deleteWave(Long id) {
        PickingWave wave = pickingWaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("波次不存在"));

        // 只有待执行状态的波次才能删除
        if (wave.getStatus() != 1) {
            throw new RuntimeException("只有待执行状态的波次才能删除");
        }

        // 检查是否有关联的拣货任务
        List<PickingTask> tasks = pickingTaskRepository.findByWaveNo(wave.getWaveNo());
        if (!tasks.isEmpty()) {
            throw new RuntimeException("波次下还有拣货任务，无法删除");
        }

        pickingWaveRepository.deleteById(id);
    }

    /**
     * 自动生成波次
     */
    @Transactional
    public PickingWaveDTO autoGenerateWave(Long warehouseId) {
        // 查找待分配库存的出库单
        List<OutboundOrder> orders = outboundOrderRepository.findByStatus(2); // 已分配库存状态
        
        if (orders.isEmpty()) {
            throw new RuntimeException("没有可生成波次的出库单");
        }

        // 生成波次号
        String waveNo = generateWaveNo();

        // 创建波次
        PickingWave wave = new PickingWave();
        wave.setWaveNo(waveNo);
        wave.setWarehouseId(warehouseId);
        wave.setStatus(1); // 待执行
        wave.setOrderCount(orders.size());
        wave.setTaskCount(0);
        wave.setCompletedTaskCount(0);
        wave.setCreatedTime(LocalDateTime.now());

        wave = pickingWaveRepository.save(wave);

        // 为出库单生成拣货任务并关联到波次
        for (OutboundOrder order : orders) {
            // 这里应该调用拣货任务生成逻辑
            // 暂时跳过具体实现
        }

        return PickingWaveMapper.toDTO(wave);
    }

    /**
     * 生成波次号
     */
    private String generateWaveNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "WAVE" + dateStr + uuid;
    }
}

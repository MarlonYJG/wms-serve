package com.bj.wms.service;

import com.bj.wms.entity.InboundOrderItem;
import com.bj.wms.entity.PutawayTask;
import com.bj.wms.repository.InboundOrderItemRepository;
import com.bj.wms.repository.PutawayTaskRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PutawayTaskService {

    private final PutawayTaskRepository putawayTaskRepository;
    private final InboundOrderItemRepository inboundOrderItemRepository;

    public Page<PutawayTask> page(Integer page, Integer size, Long inboundOrderId, Integer status) {
        int pageNumber = page == null || page < 1 ? 0 : page - 1;
        int pageSize = size == null || size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Specification<PutawayTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (inboundOrderId != null) {
                // 通过子查询命中过滤 inboundOrderItemId 属于该入库单的任务
                var sub = query.subquery(Long.class);
                var item = sub.from(InboundOrderItem.class);
                sub.select(item.get("id")).where(cb.equal(item.get("inboundOrderId"), inboundOrderId));
                predicates.add(root.get("inboundOrderItemId").in(sub));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return putawayTaskRepository.findAll(spec, pageable);
    }

    public List<PutawayTask> listByInboundOrderId(Long inboundOrderId) {
        // 复用分页查询的 where 仅不分页
        return page(1, Integer.MAX_VALUE, inboundOrderId, null).getContent();
    }

    public Optional<PutawayTask> findById(Long id) {
        return putawayTaskRepository.findById(id);
    }

    @Transactional
    public List<PutawayTask> generate(Long inboundOrderItemId, String putawayStrategy) {
        InboundOrderItem item = inboundOrderItemRepository.findById(inboundOrderItemId)
                .orElseThrow(() -> new IllegalArgumentException("入库明细不存在"));
        // 简化：按明细的 receivedQuantity 生成单条上架任务，策略暂不生效
        int quantity = item.getReceivedQuantity() == null ? 0 : item.getReceivedQuantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("该入库明细暂无可上架数量");
        }
        PutawayTask task = new PutawayTask();
        task.setTaskNo(generateTaskNo());
        task.setInboundOrderItemId(inboundOrderItemId);
        task.setQuantity(quantity);
        // 简化：由前端或后续接口指定上架库位，这里先占位为 0，避免空指针
        task.setToLocationId(0L);
        task.setStatus(1);
        PutawayTask saved = putawayTaskRepository.save(task);
        return java.util.Collections.singletonList(saved);
    }

    @Transactional
    public PutawayTask start(Long taskId, Integer operatorId) {
        PutawayTask task = putawayTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("上架任务不存在"));
        if (task.getStatus() != null && task.getStatus() >= 2) {
            return task; // 已开始/完成不重复处理
        }
        task.setStatus(2);
        if (operatorId != null) {
            task.setOperator(operatorId);
        }
        return putawayTaskRepository.save(task);
    }

    @Transactional
    public PutawayTask complete(Long taskId) {
        PutawayTask task = putawayTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("上架任务不存在"));
        task.setStatus(3);
        return putawayTaskRepository.save(task);
    }

    private String generateTaskNo() {
        String no = "PT" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        if (putawayTaskRepository.existsByTaskNo(no)) {
            return generateTaskNo();
        }
        return no;
    }
}



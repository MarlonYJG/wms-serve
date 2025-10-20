package com.bj.wms.service;

import com.bj.wms.entity.InboundQc;
import com.bj.wms.entity.QcStatus;
import com.bj.wms.repository.InboundQcRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InboundQcService {

    private final InboundQcRepository inboundQcRepository;

    public Page<InboundQc> page(Integer page, Integer size, Long inboundOrderItemId, Integer status) {
        int pageNumber = page == null || page < 1 ? 0 : page - 1;
        int pageSize = size == null || size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Specification<InboundQc> spec = (root, query, cb) -> {
            java.util.List<Predicate> predicates = new ArrayList<>();
            if (inboundOrderItemId != null) {
                predicates.add(cb.equal(root.get("inboundOrderItemId"), inboundOrderItemId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), QcStatus.fromCode(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return inboundQcRepository.findAll(spec, pageable);
    }

    @Transactional
    public InboundQc create(Long inboundOrderItemId, String remark) {
        if (inboundOrderItemId == null) {
            throw new IllegalArgumentException("入库明细ID不能为空");
        }
        InboundQc qc = new InboundQc();
        qc.setInboundOrderItemId(inboundOrderItemId);
        qc.setStatus(QcStatus.PENDING);
        qc.setQualifiedQuantity(0);
        qc.setUnqualifiedQuantity(0);
        qc.setRemark(remark);
        return inboundQcRepository.save(qc);
    }

    public Optional<InboundQc> detail(Long id) {
        return inboundQcRepository.findById(id);
    }

    @Transactional
    public InboundQc submitResult(Long id, Integer qualifiedQuantity, Integer unqualifiedQuantity, String remark) {
        InboundQc qc = inboundQcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("质检记录不存在"));
        int qualified = qualifiedQuantity == null ? 0 : Math.max(0, qualifiedQuantity);
        int unqualified = unqualifiedQuantity == null ? 0 : Math.max(0, unqualifiedQuantity);
        qc.setQualifiedQuantity(qualified);
        qc.setUnqualifiedQuantity(unqualified);
        if (remark != null) {
            qc.setRemark(remark);
        }
        qc.setStatus(QcStatus.COMPLETED);
        return inboundQcRepository.save(qc);
    }
}



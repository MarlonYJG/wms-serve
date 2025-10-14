package com.bj.wms.repository;

import com.bj.wms.entity.InboundOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InboundOrderItemRepository extends JpaRepository<InboundOrderItem, Long>, JpaSpecificationExecutor<InboundOrderItem> {
}



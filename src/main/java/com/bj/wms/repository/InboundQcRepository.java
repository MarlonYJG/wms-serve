package com.bj.wms.repository;

import com.bj.wms.entity.InboundQc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InboundQcRepository extends JpaRepository<InboundQc, Long>, JpaSpecificationExecutor<InboundQc> {
}



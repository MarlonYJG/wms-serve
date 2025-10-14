package com.bj.wms.repository;

import com.bj.wms.entity.PutawayTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PutawayTaskRepository extends JpaRepository<PutawayTask, Long>, JpaSpecificationExecutor<PutawayTask> {
    boolean existsByTaskNo(String taskNo);
}



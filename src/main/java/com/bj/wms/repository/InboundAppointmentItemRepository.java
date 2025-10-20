package com.bj.wms.repository;

import com.bj.wms.entity.InboundAppointmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InboundAppointmentItemRepository extends JpaRepository<InboundAppointmentItem, Long>, JpaSpecificationExecutor<InboundAppointmentItem> {
    
    /**
     * 根据预约单ID查询明细
     */
    List<InboundAppointmentItem> findByAppointmentId(Long appointmentId);
    
    /**
     * 根据预约单ID删除明细
     */
    void deleteByAppointmentId(Long appointmentId);
    
    /**
     * 统计预约单的总预估数量
     */
    @Query("SELECT COALESCE(SUM(i.expectedQuantity), 0) FROM InboundAppointmentItem i WHERE i.appointmentId = :appointmentId")
    Integer sumExpectedQuantityByAppointmentId(@Param("appointmentId") Long appointmentId);
}

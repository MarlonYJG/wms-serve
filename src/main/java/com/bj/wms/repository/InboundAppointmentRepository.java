package com.bj.wms.repository;

import com.bj.wms.entity.AppointmentStatus;
import com.bj.wms.entity.InboundAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface InboundAppointmentRepository extends JpaRepository<InboundAppointment, Long>, JpaSpecificationExecutor<InboundAppointment> {
    
    boolean existsByAppointmentNo(String appointmentNo);
    
    /**
     * 统计指定日期和时间段内的预约数量
     */
    long countByAppointmentDateAndAppointmentTimeStartBetweenAndWarehouseIdAndStatus(
        LocalDate appointmentDate, 
        LocalTime startTime, 
        LocalTime endTime, 
        Long warehouseId, 
        AppointmentStatus status
    );
    
    /**
     * 根据ID查询预约单详情，包含明细数据
     */
    @Query("SELECT a FROM InboundAppointment a LEFT JOIN FETCH a.appointmentItems WHERE a.id = :id")
    Optional<InboundAppointment> findByIdWithItems(@Param("id") Long id);
}



package com.bj.wms.repository;

import com.bj.wms.entity.InboundAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InboundAppointmentRepository extends JpaRepository<InboundAppointment, Long>, JpaSpecificationExecutor<InboundAppointment> {
    boolean existsByAppointmentNo(String appointmentNo);
}



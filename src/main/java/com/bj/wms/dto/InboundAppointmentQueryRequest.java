package com.bj.wms.dto;

import lombok.Data;

@Data
public class InboundAppointmentQueryRequest {
    
    private Integer page = 1;
    
    private Integer size = 10;
    
    private Long warehouseId;
    
    private Long supplierId;
    
    private Integer status;
    
    private String appointmentDate;
    
    private String appointmentNo;
}

package com.bj.wms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InboundAppointmentItemDTO {
    private Long id;
    
    private Long appointmentId;
    
    @NotNull
    private Long productSkuId;
    
    private String productName;
    
    private String skuCode;
    
    @NotNull
    private Integer expectedQuantity;
    
    private BigDecimal unitPrice;
    
    private String batchNo;
    
    private LocalDate productionDate;
    
    private LocalDate expiryDate;
}

package com.bj.wms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InboundAppointmentItemCreateRequest {
    
    @NotNull
    private Long productSkuId;
    
    @NotNull
    @Positive
    private Integer expectedQuantity;
    
    private BigDecimal unitPrice;
    
    private String batchNo;
    
    private LocalDate productionDate;
    
    private LocalDate expiryDate;
}

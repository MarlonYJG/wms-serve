package com.bj.wms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ConfirmReceiptRequest {
    @NotEmpty(message = "收货明细不能为空")
    @Valid
    private List<ReceiptItemRequest> items;
    
    @Data
    public static class ReceiptItemRequest {
        private Long productSkuId;
        private Integer receivedQuantity;
        private String batchNo;
        private String productionDate;
        private String expiryDate;
    }
}

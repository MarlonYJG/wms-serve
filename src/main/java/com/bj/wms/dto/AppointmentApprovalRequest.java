package com.bj.wms.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppointmentApprovalRequest {
    
    private boolean approved;
    
    @Size(max = 500)
    private String remarks;
}

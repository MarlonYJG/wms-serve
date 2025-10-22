package com.bj.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 复核任务完成请求
 */
@Data
public class ReviewTaskCompleteRequest {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "实际数量不能为空")
    @Min(value = 0, message = "实际数量不能小于0")
    private Integer actualQuantity;

    private String remark; // 备注
}

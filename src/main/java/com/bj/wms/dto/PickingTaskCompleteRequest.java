package com.bj.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 拣货任务完成请求
 */
@Data
public class PickingTaskCompleteRequest {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "拣货数量不能为空")
    @Min(value = 1, message = "拣货数量必须大于0")
    private Integer pickedQuantity;

    private String remark; // 备注
}
